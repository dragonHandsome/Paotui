<template>
  <div class="user-service">
    <ul class="news-list">
      <template v-for="(row, $index) in newsList">
        <li
          :class="curNewsIndex === $index && 'active'"
          v-if="row.fromUserId == 0"
          :key="row.createdTime + row.id  + row.fromUserId "
          @click="handleSwitch(row, $index)"
        >
          <div class="before">
            <el-image fit="cover" :src="sysAvatar" />
          </div>
          <div class="hot" v-if="row.noReadCount != 0">{{ row.noReadCount }}</div>
          <div class="name">系统消息</div>
          <div class="content">用户:{{row.toUser.name}},请求客服服务。</div>
        </li>
        <li
          v-else
          :key="row.createdTime + row.id  + row.fromUserId"
          :class="curNewsIndex === $index && 'active'"
          @click="handleSwitch(row, $index)"
        >
          <div class="before">
            <el-image fit="cover" :src="row.fromUser.avatar | handleImg" />
          </div>
          <div class="hot" v-if="row.noReadCount != 0">{{ row.noReadCount }}</div>
          <div class="name">{{ row.fromUser.name }}</div>
          <div class="content">{{ row.content | contentFilter}}</div>
        </li>
      </template>
    </ul>
    <div class="chat" v-if="isSystemNotify">
      <ul class="chat-thread">
        <template v-for="row of chatList">
          <li class="TA" :key="row.createdTime + row.id  + row.fromUserId">
            <div class="before">
              <el-image fit="cover" style="width: 50px; height: 50px" :src="sysAvatar"></el-image>
            </div>
            <div class="content">用户编号: {{row.toUserId}}，名字: {{ row.toUser.name }}，发起客服求助。</div>
            <el-button v-if="!row.isRead" class="chat-accept" @click="handleAccept(row)">接收</el-button>
          </li>
        </template>
      </ul>
    </div>
    <div class="chat" v-else>
      <ul class="chat-thread">
        <template v-for="row of chatList">
          <li
            :class="row.fromUserId > 0 ? 'TA' : ''"
            :key="row.createdTime + row.id  + row.fromUserId"
          >
            <div class="before">
              <el-image
                fit="cover"
                style="width: 50px; height: 50px"
                :src="row.fromUser && row.fromUser.avatar | handleImg"
              ></el-image>
            </div>
            <div class="content">{{ row.content | contentFilter}}</div>
          </li>
        </template>
      </ul>
      <div class="chat-form" v-if="curNews && curNews.noReadCount > 0">
        <el-form>
          <el-input
            type="textarea"
            v-model="contents[curSessionUserId]"
            @keyup.enter.native="handleSend()"
          />
          <div class="chat-form-option">
            <el-button @click="handleComplete()">完成</el-button>
            <el-button @click="handleCancel()">撤销</el-button>
            <el-button class="chat-form-send" @click="handleSend()">发送</el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>
<script>
import store from "@/store";
import { handleImg } from "@/utils";
import {
  connect,
  getNewsListSort,
  getUserNotifies,
  send,
  readByIds
} from "@/utils/websocket";

/**
 * CHAT,
    TO_USER,
    TO_ADMIN,
    COMPLETE_SESSION,
    ACCEPT_SESSION,
    CANCEL_SESSION
 */
const defaultMessage = {
  type: "TO_USER",
  toUserId: "",
  content: ""
};

export default {
  filters: {
    handleImg,
    contentFilter(data) {
      if (data && typeof data === "object") {
        return data.msg;
      }
      return data;
    }
  },
  data() {
    return {
      newsList: [],
      curNews: null,
      chatList: [],
      contents: {},
      curSessionUserId: 0,
      curNewsIndex: 0,
      message: Object.assign({}, defaultMessage),
      isSystemNotify: true,
      curUserId: -store.getters.id, // 要负 因为通知中存管理为负数
      sysAvatar: process.env.VUE_APP_BASE_API + "/uploads/sysImg"
    };
  },
  created() {
    this.connectWebSocket();
  },
  methods: {
    connectWebSocket() {
      connect(store, this.handleMessage);
      this.getNewsList();
      this.initSysChatWindow();
    },
    initSysChatWindow() {
      this.chatList = getUserNotifies(this.curSessionUserId);
    },
    getNewsList() {
      const newsList = getNewsListSort();
      const sysIndex = newsList.findIndex(cur => cur.fromUserId == 0);
      const systemNews = newsList.splice(sysIndex, 1)[0];
      console.log(newsList);
      if (systemNews != null) {
        newsList.unshift(systemNews);
      } else {
        newsList.unshift({
          fromUserId: 0,
          id: -213213123123,
          isRead: true,
          toUser: {
            avatar: "/uploads/systemImg",
            name: "还没有人"
          },
          noReadCount: 0
        });
      }
      console.log("console.log(newsList)");
      console.log(newsList);
      this.newsList = newsList;
      if (newsList != null) this.curNews = newsList[this.curNewsIndex];
    },
    /**
     * COMPLETE_SESSION,
    ACCEPT_SESSION,
    CANCEL_SESSION
     */
    handleMessage(message, notify) {
      this.updateNewsListSort();
      this.updateUserNotifies();
      if (notify.fromUserId == this.curSessionUserId) {
        this.adjustWindow();
      }
    },
    adjustWindow() {
      this.$nextTick(() => {
        document.querySelectorAll(".chat-thread").forEach(cur => {
          cur.scrollTop = cur.scrollHeight;
        });
      });
    },
    updateNewsListSort() {
      this.getNewsList();
    },
    updateUserNotifies() {
      this.chatList = getUserNotifies(this.curSessionUserId);
      this.adjustWindow();
    },
    handleSwitch(row, $index) {
      const curSessionUserId = row.fromUserId;
      this.curSessionUserId = curSessionUserId;
      this.message.content = this.contents.curSessionUserId;
      this.message.toUserId = curSessionUserId;
      this.updateUserNotifies();
      this.curNews = row;
      this.curNewsIndex = $index;
      this.isSystemNotify = curSessionUserId === 0 ? true : false;
      //不是系统通知则直接读 系统通知需通过接收
      if (!this.isSystemNotify) {
        this.updateNewsListSort();
      }
    },
    handleComplete() {
      this.message.type = "COMPLETE_SESSION";
      this.sendMessage();
      this.readLocalChatList();
      this.updateUserNotifies();
      this.updateNewsListSort();
      this.$message({
        type: "success",
        message: "Complete session Success"
      });
    },
    readLocalChatList() {
      const isReadIds = [];
      this.chatList.forEach(cur => {
        //未读就读
        if (!cur.isRead) {
          isReadIds.push(cur.id);
        }
      });
      readByIds(isReadIds);
    },
    handleCancel() {
      this.message.type = "CANCEL_SESSION";
      this.sendMessage();
      this.readLocalChatList();
      this.updateUserNotifies();
      this.updateNewsListSort();
      this.$message({
        type: "success",
        message: "Cancel session Success"
      });
    },
    handleAccept(row) {
      this.message.type = "ACCEPT_SESSION";
      this.message.toUserId = row.toUserId;
      //接收哪个读哪个
      readByIds([row.id]);
      this.sendMessage();
      this.$notify({
        title: "Success",
        dangerouslyUseHTMLString: true,
        message: `
            <div>Accpet User Key: ${row.toUserId}</div>
            <div>User Name: ${row.toUser.name}</div>
          `,
        type: "success"
      });
    },
    handleSend(row) {
      this.message.type = "TO_USER";
      this.message.content = this.contents[this.curSessionUserId];
      if (!this.message.content) {
        this.$message({
          type: "error",
          message: "不能发空内容！"
        });
        return;
      }
      this.sendMessage();
      this.contents[this.curSessionUserId] = "";
    },
    sendMessage() {
      send(JSON.stringify(this.message));
    }
  }
};
</script>
<style lang="scss">
.user-service {
  position: absolute;
  width: 100%;
  height: 100%;
  padding: 0;
  margin: 0 auto;
  display: flex;
  background: -moz-linear-gradient(
    -45deg,
    #183850 0,
    #183850 25%,
    #192c46 50%,
    #22254c 75%,
    #22254c 100%
  );
  background: -webkit-linear-gradient(
    -45deg,
    #183850 0,
    #183850 25%,
    #192c46 50%,
    #22254c 75%,
    #22254c 100%
  );
  background-repeat: no-repeat;
  background-attachment: fixed;
}
::-webkit-scrollbar {
  width: 10px;
}

::-webkit-scrollbar-track {
  border-radius: 10px;
  background-color: rgba(25, 147, 147, 0.1);
}

::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background-color: rgba(25, 147, 147, 0.2);
}
.chat {
  width: 100%;
}
.chat-thread {
  margin-top: 24px;
  margin-left: 30px;
  padding: 0 20px 0 0;
  list-style: none;
  overflow-y: scroll;
  overflow-x: hidden;
  transition: all 0.15s;
}

.chat-thread li {
  position: relative;
  clear: both;
  display: inline-block;
  padding: 16px 40px 16px 20px;
  margin: 0 0 20px 0;
  font: 16px/20px "Noto Sans", sans-serif;
  border-radius: 10px;
  background-color: rgba(25, 147, 147, 0.2);
}

/* Chat - Avatar */
.chat-thread li .before {
  position: absolute;
  top: 0;
  width: 50px;
  height: 50px;
  border-radius: 50px;
  overflow: hidden;
}

/* Chat - Speech Bubble Arrow */
.chat-thread li:after {
  position: absolute;
  top: 15px;
  content: "";
  width: 0;
  height: 0;
  border-top: 15px solid rgba(25, 147, 147, 0.2);
}

.chat-thread li {
  animation: show-chat-odd 0.15s 1 ease-in;
  -moz-animation: show-chat-odd 0.15s 1 ease-in;
  -webkit-animation: show-chat-odd 0.15s 1 ease-in;
  float: right;
  margin-right: 80px;
  color: #0ad5c1;
}

.chat-thread li .before {
  right: -80px;
}

.chat-thread li:after {
  border-right: 15px solid transparent;
  right: -15px;
}

.chat-thread li.TA {
  animation: show-chat-even 0.15s 1 ease-in;
  -moz-animation: show-chat-even 0.15s 1 ease-in;
  -webkit-animation: show-chat-even 0.15s 1 ease-in;
  float: left;
  margin-left: 80px;
  color: #0ec879;
}

.chat-thread li.TA .before {
  left: -80px;
}

.chat-thread li.TA:after {
  border-left: 15px solid transparent;
  border-right: none;
  left: -15px;
}

.chat-window {
  position: fixed;
  bottom: 18px;
}

.chat-window-message {
  width: 100%;
  height: 48px;
  font: 32px/48px "Noto Sans", sans-serif;
  background: none;
  color: #0ad5c1;
  border: 0;
  border-bottom: 1px solid rgba(25, 147, 147, 0.2);
  outline: none;
}

.news-list {
  list-style: none;
  color: orange;
  height: 600px;
  overflow-y: scroll;
  padding: 0 20px;
  margin: 20px 0;
}
.news-list li {
  position: relative;
  cursor: pointer;
  overflow-x: hidden;
  padding: 5px 10px 20px 70px;
  min-width: 70px;
  animation: show-news-list 0.15s 1 ease-in;
  list-style: none;
  font-size: 1rem;
}
.news-list li .content {
  margin-top: 10px;
  font-size: 14px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  color: rgba(25, 147, 147, 0.8);
}
.news-list li:hover,
.news-list li.active {
  filter: brightness(120%);
  background-color: rgba(25, 147, 147, 0.1);
}
.news-list li .before {
  position: absolute;
  top: 0;
  left: 10px;
  width: 50px;
  height: 50px;
  background-size: cover;
  border-radius: 50px;
  overflow: hidden;
}
.news-list li .hot {
  position: absolute;
  right: 10px;
  top: 14px;
  margin: auto 0;
  border-radius: 50%;
  background-color: orange;
  padding: 3px 5px;
  font-size: 8px;
  color: #fff;
}
.news-list li .before img {
  width: 50px;
  height: 50px;
}
.chat-form {
  position: relative;
  width: 50%;
  margin-left: 10px;
}
.chat-form-option {
  position: absolute;
  top: 0;
  right: -30px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  font-size: 24px;
}
.user-service button {
  margin-left: 0 !important;
  background-color: rgba(14, 85, 85, 0.5);
  color: aquamarine;
  border: none;
  margin-top: 10px;
}
.chat-form-send {
  margin-top: 22px !important;
  padding: 14px 24px;
}
.user-service button:hover {
  background-color: rgba(14, 85, 85, 0.5);
  color: aquamarine;
  filter: brightness(130%);
}
.chat-form textarea {
  width: 90%;
  margin-left: 14px;
  height: 120px;
  color: aquamarine;
  box-sizing: border-box;
  border-right: 0px solid rgba(25, 147, 147, 0.2);
  resize: none;
  border: none;
  border-radius: 5px;
  background-color: rgba(25, 147, 147, 0.1);
  border-bottom: 1px solid rgba(55, 147, 147, 0.4);
}
.chat-form textarea:hover,
.chat-form textarea:focus {
  border-bottom: 1px solid rgba(55, 147, 147, 0.8);
}
.chat-accept {
  float: right;
}
@media all and (max-width: 767px) {
  .chat-thread {
    width: 90%;
    height: 425px;
  }
  .chat-form {
    width: 90%;
    height: 155px;
  }
  .news-list li {
    width: 50px;
    padding-right: 0;
  }
}
/* Medium and large screens */
@media all and (min-width: 768px) {
  .chat-thread {
    width: 80%;
    height: 425px;
  }
  .chat-form {
    width: 80%;
    height: 175px;
  }
  .news-list {
    min-width: 300px;
  }
}
@media all and (min-width: 1920px) {
  .chat-thread {
    width: 50%;
    height: 425px;
  }
  .chat-form {
    width: 50%;
    height: 175px;
  }
}
@keyframes show-chat-even {
  0% {
    margin-left: -480px;
  }

  100% {
    margin-left: 0;
  }
}
@-moz-keyframes show-chat-even {
  0% {
    margin-left: -480px;
  }

  100% {
    margin-left: 0;
  }
}
@-webkit-keyframes show-chat-even {
  0% {
    margin-left: -480px;
  }

  100% {
    margin-left: 0;
  }
}
@keyframes show-chat-odd {
  0% {
    margin-right: -480px;
  }

  100% {
    margin-right: 0;
  }
}
@-moz-keyframes show-chat-odd {
  0% {
    margin-right: -480px;
  }

  100% {
    margin-right: 0;
  }
}
@-webkit-keyframes show-chat-odd {
  0% {
    margin-right: -480px;
  }

  100% {
    margin-right: 0;
  }
}
@keyframes show-news-list {
  0% {
    margin-left: -30px;
    opacity: 0.6;
  }
  100% {
    margin-left: 0;
    opacity: 1;
  }
}
</style>
