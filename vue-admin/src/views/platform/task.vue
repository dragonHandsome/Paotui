<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.title"
        placeholder="Title"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-select
        v-model="listQuery.star"
        placeholder="Star"
        clearable
        style="width: 90px"
        class="filter-item"
      >
        <el-option v-for="item in starOptions" :key="item" :label="item" :value="item" />
      </el-select>
      <el-select
        v-model="listQuery.categoryId"
        placeholder="Category"
        clearable
        class="filter-item"
        style="width: 130px"
      >
        <el-option
          v-for="item in CategoryOptions"
          :key="item.id"
          :label="item.display_name+'('+item.key+')'"
          :value="item.id"
        />
      </el-select>
      <el-select
        v-model="listQuery.status"
        placeholder="Status"
        clearable
        class="filter-item"
        style="width: 130px"
      >
        <el-option
          v-for="item in StatusOptions"
          :key="item.key"
          :label="item.display_name+'('+item.key+')'"
          :value="item.key"
        />
      </el-select>
      <el-select
        v-model="listQuery.sort"
        style="width: 140px"
        class="filter-item"
        @change="handleFilter"
      >
        <el-option
          v-for="item in sortOptions"
          :key="item.key"
          :label="item.label"
          :value="item.key"
        />
      </el-select>
      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="handleFilter"
      >Search</el-button>
    </div>

    <el-table
      :key="tableKey"
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%;"
      @sort-change="sortChange"
    >
      <el-table-column
        label="ID"
        prop="id"
        sortable="custom"
        align="center"
        width="80"
        :class-name="getSortClass('id')"
      >
        <template slot-scope="{row}">
          <span>{{ row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Create Date" width="150px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Title" min-width="120px">
        <template slot-scope="{row}">
          <span class="link-type" @click="handleShow(row)">{{ row.title }}</span>
          <el-tag>{{ row.categoryId | categoryFilter }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="From u no" width="110px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.fromUserId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="To u no" width="110px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.toUserId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Reward" width="110px" align="center">
        <template slot-scope="{row}">
          <span style="color: red">{{ row.rewardMoney | moneyFilter }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Status" class-name="status-col" width="100">
        <template slot-scope="{row}">
          <el-tag :type="row.status | statusFilter">{{ row.status | typeFilter}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="star" width="120px">
        <template slot-scope="{row}">
          <svg-icon
            v-for="n in + row.star"
            :key="n"
            icon-class="star"
            style="color: #FF534D; margin-right: 4px;"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="Actions"
        align="center"
        width="230"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="{row,$index}">
          <el-popover v-model="visible[$index]" trigger="click" placement="top" width="400">
            <el-form v-if="visible.cur == $index" :model="taskHandleRecord">
              <el-form-item label="Reason">
                <el-input
                  v-model="taskHandleRecord.reason"
                  autocomplete="off"
                  :autofocus="visible.cur == $index"
                ></el-input>
              </el-form-item>
            </el-form>
            <div style="text-align: right; margin: 0">
              <el-button size="mini" type="text" @click="visible[$index] = false">取消</el-button>
              <el-button type="primary" size="mini" @click="handleCancelTask(row, $index)">确定</el-button>
            </div>
            <el-button
              v-if="(row.status!='COMPLETE' && row.status != 'CANCELED')"
              slot="reference"
              size="mini"
              type="danger"
              @click="handleCancel(row,$index)"
            >违规取消</el-button>
          </el-popover>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />
    <el-dialog :visible.sync="dialogFormVisible" custom-class="task_cancel_dialog">
      <template>
        <section>
          <h2 style="margin-bottom: 30px">任务详情</h2>
          <el-form disabled label-width="100px" label-position="left" size="mini">
            <el-form-item label="Task key">
              <el-input v-model="temp.id"></el-input>
            </el-form-item>
            <el-form-item label="Task title">
              <el-input v-model="temp.title"></el-input>
            </el-form-item>
            <el-form-item label="Task content">
              <el-input v-model="temp.content" type="textarea" :autosize="{ minRows: 4}"></el-input>
            </el-form-item>
            <template v-for="row in temp.options">
              <el-form-item :key="row.id" :label="row.name">
                <el-input v-model="row.value" />
              </el-form-item>
            </template>
            <el-form-item label="Cert images">
              <template v-if="(temp.images instanceof Array && temp.images.length)">
                <el-tooltip class="item" effect="dark" content="点击显示全部图片" placement="top-end">
                  <el-image
                    fit="cover"
                    style="width: 300px"
                    :src="temp.images[0]"
                    :preview-src-list="temp.images"
                  />
                </el-tooltip>
              </template>
            </el-form-item>
          </el-form>
        </section>
      </template>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">Cancel</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getTasks, getTaskDetail, handleCancelTask } from "@/api/task";
import waves from "@/directive/waves"; // waves directive
import { parseTime } from "@/utils";
import Pagination from "@/components/Pagination"; // secondary package based on el-pagination
// import { deepClone } from "@/utils";

const StatusOptions = [
  { key: "AWAIT_TAKEN", display_name: "新发布" },
  { key: "AWAIT_COMMIT", display_name: "待提交" },
  { key: "AWAIT_CONFIRM", display_name: "待确认" },
  { key: "REQ_CANCEL", display_name: "申请取消" },
  { key: "COMPLETE", display_name: "已完成" },
  { key: "CANCELED", display_name: "已取消" }
];

const CategoryOptions = [
  { id: 1, key: "express", display_name: "寄取快递" },
  { id: 2, key: "library", display_name: "借还书" },
  { id: 3, key: "issue", display_name: "求解问题" },
  { id: 4, key: "buy", display_name: "代买，代打包" }
];

// arr to obj, such as { CN : "China", US : "USA" }
const StatusTypeKeyValue = StatusOptions.reduce((acc, cur) => {
  acc[cur.key] = cur.display_name;
  return acc;
}, {});

const CategoryKeyValue = CategoryOptions.reduce((acc, cur) => {
  acc[cur.id] = cur.display_name;
  return acc;
}, {});

const taskDetail = {
  categoryId: 1,
  content: "oo",
  createTime: "",
  title: "你好"
};

const taskCertification = {
  content: "",
  images: [],
  createTime: ""
};

const taskApplyCancelData = {
  content: "",
  images: [],
  createTime: "",
  status: ""
};

const taskHandleRecord = {
  reason: "",
  taskId: "",
  type: "illegal_cancel"
};

export default {
  name: "Task",
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        COMPLETE: "success",
        CANCELED: "info",
        REQ_CANCEL: "danger"
      };
      return statusMap[status] || "info";
    },
    typeFilter(type) {
      return StatusTypeKeyValue[type];
    },
    categoryFilter(id) {
      return CategoryKeyValue[id];
    },
    moneyFilter(money) {
      return "￥" + (money / 100).toFixed(2);
    }
  },
  data() {
    return {
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      parseTime,
      taskDetail,
      taskCertification,
      taskApplyCancelData,
      taskHandleRecord: Object.assign({}, taskHandleRecord),
      visible: {},
      listQuery: {
        page: 1,
        limit: 20,
        star: undefined,
        title: undefined,
        status: undefined,
        categoryId: undefined,
        sort: "-id"
      },
      temp: {},
      starOptions: [1, 2, 3, 4, 5],
      StatusOptions,
      CategoryOptions,
      sortOptions: [
        { label: "ID Ascending", key: "+id" },
        { label: "ID Descending", key: "-id" }
      ],
      dialogFormVisible: false,
      dialogStatus: "",
      textMap: {
        update: "Edit",
        create: "Create"
      },
      dialogPvVisible: false,
      pvData: [],
      downloadLoading: false
    };
  },
  created() {
    this.getList();
  },
  methods: {
    async getList() {
      this.listLoading = true;
      const res = await getTasks(this.listQuery);
      const pageInfo = res.data;
      this.list = pageInfo.list;
      this.total = pageInfo.total;
      setTimeout(() => {
        this.listLoading = false;
      }, 1 * 1000);
    },
    handleImg(src) {
      function dealSrc(src) {
        console.log(src);
        if (!src.startsWith("http") && !src.startsWith("//")) {
          console.log(process.env.VUE_APP_BASE_API + src);
          return process.env.VUE_APP_BASE_API + src;
        }
        return src;
      }
      if (typeof src === "string") {
        return dealSrc(src);
      } else if (src instanceof Array) {
        src.forEach((cur, i) => (src[i] = dealSrc(cur)));
        return src;
      }
      return src;
    },
    handleFilter() {
      this.listQuery.page = 1;
      this.getList();
    },
    handleCancel(row, $index) {
      this.taskHandleRecord.taskId = row.id;
      this.taskHandleRecord.reason = "";
      this.$nextTick(() => {
        this.visible.cur = $index;
      });
    },
    async handleCancelTask(row, $index) {
      this.visible[$index] = false;
      await handleCancelTask(this.taskHandleRecord);
      row.status = "CANCELED";
      this.$notify({
        title: "Success",
        message: "Cancel Successfully",
        type: "success",
        duration: 2000
      });
    },
    handleModifyStatus(row, status) {
      this.$message({
        message: "操作Success",
        type: "success"
      });
      row.status = status;
    },
    sortChange(data) {
      const { prop, order } = data;
      if (prop === "id") {
        this.sortByID(order);
      }
    },
    sortByID(order) {
      if (order === "ascending") {
        this.listQuery.sort = "+id";
      } else {
        this.listQuery.sort = "-id";
      }
      this.handleFilter();
    },
    async getTaskDetail(id) {
      const { data: temp } = await getTaskDetail(id);
      const img_index = temp.wechatOptions.findIndex(
        option => option.type === "IMAGES"
      );
      if (img_index !== -1) {
        temp.images = temp.wechatOptions.splice(img_index, 1);
      }
      if (temp.images instanceof Array) {
        temp.images = temp.images[0].value.split(";");
      }
      this.handleImg(temp.images);
      temp.options = temp.wechatOptions;
      this.temp = temp;
      this.dialogFormVisible = true;
    },
    handleShow(row) {
      this.getTaskDetail(row.id); // copy obj
    },
    getSortClass: function(key) {
      const sort = this.listQuery.sort;
      return sort === `+${key}` ? "ascending" : "descending";
    }
  }
};
</script>
