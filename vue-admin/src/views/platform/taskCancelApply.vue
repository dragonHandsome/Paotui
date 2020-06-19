<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.taskId"
        placeholder="TaskId"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <div class="filter-item">
        <el-date-picker
          v-model="dateBound"
          type="daterange"
          align="left"
          unlink-panels
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :picker-options="pickerOptions"
        />
      </div>
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
      <el-table-column label="Task key" width="120px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.taskId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Task title" min-width="120px">
        <template slot-scope="{row}">
          <span class="link-type" @click="handleShow(row)">{{ row.task.title }}</span>
          <el-tag>{{ row.task.rewardMoney | moneyFilter }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Reason" min-width="150px">
        <template slot-scope="{row}">
          <span>{{ row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Status" class-name="status-col" width="100">
        <template slot-scope="{row}">
          <el-tag :type="row.status | statusFilter">{{ row.status | typeFilter }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="Actions"
        align="center"
        width="230"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="{row,$index}">
          <el-popover trigger="click" placement="top" width="400" v-model="visible[$index]">
            <el-form :model="taskHandleRecord" v-if="visible.cur == $index">
              <el-form-item label="Reason" >
                <el-input v-model="taskHandleRecord.reason" autocomplete="off" :autofocus="visible.cur == $index"></el-input>
              </el-form-item>
            </el-form>
            <div style="text-align: right; margin: 0">
              <el-button size="mini" type="text" @click="visible[$index] = false">取消</el-button>
              <el-button type="primary" size="mini" @click="handleCancelTask(row, $index)">确定</el-button>
            </div>
            <el-button
              v-if="(row.status!='COMPLETE' && row.status != 'CANCELED')"
              size="mini"
              type="primary"
              slot="reference"
              @click="handleCancel(row,$index)"
            >取消通过</el-button>
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
    <el-dialog
      :title="'Reference'"
      :visible.sync="dialogFormVisible"
      custom-class="task_cancel_dialog"
    >
      <template>
        <section>
          <h2 style="margin-bottom: 30px">取消理由</h2>
          <el-form disabled label-width="100px" label-position="left" size="mini">
            <el-form-item label="key" max-width="300px">
              <el-input v-model="temp.id" />
            </el-form-item>
            <el-form-item label="Reason">
              <el-input v-model="temp.content" type="textarea" :autosize="{ minRows: 4 }" />
            </el-form-item>
            <el-form-item label="Cert Image">
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
      <template>
        <section>
          <el-tooltip class="item" effect="dark" content="任务尚未提交" placement="top-start">
            <h2 style="margin-bottom: 30px">任务提交凭证</h2>
          </el-tooltip>
          <el-form
            v-if="taskCertification"
            disabled
            label-width="100px"
            label-position="left"
            size="mini"
          >
            <el-form-item label="key">
              <el-input v-model="taskCertification.id" />
            </el-form-item>
            <el-form-item label="Cert content">
              <el-input
                v-model="taskCertification.content"
                type="textarea"
                :autosize="{ minRows: 4 }"
              />
            </el-form-item>
            <el-form-item label="Cert images">
              <template
                v-if="(taskCertification.images instanceof Array && taskCertification.images.length)"
              >
                <el-tooltip class="item" effect="dark" content="点击显示全部图片" placement="top-end">
                  <el-image
                    fit="cover"
                    style="width: 300px"
                    :src="taskCertification.images[0]"
                    :preview-src-list="taskCertification.images"
                  />
                </el-tooltip>
              </template>
            </el-form-item>
          </el-form>
        </section>
      </template>
      <template>
        <section>
          <h2 style="margin-bottom: 30px">任务详情</h2>
          <el-form disabled label-width="100px" label-position="left" size="mini">
            <el-form-item label="Task key">
              <el-input v-model="temp.task.id" />
            </el-form-item>
            <el-form-item label="Task title">
              <el-input v-model="temp.task.title" />
            </el-form-item>
            <el-form-item label="Task content">
              <el-input v-model="temp.task.content" type="textarea" :autosize="{ minRows: 4}" />
            </el-form-item>
            <template v-for="row in temp.task.options">
              <el-form-item :key="row.id" :label="row.name">
                <el-input v-model="row.value" />
              </el-form-item>
            </template>
            <el-form-item label="Cert images">
              <template v-if="(temp.task.images instanceof Array && temp.task.images.length)">
                <el-tooltip class="item" effect="dark" content="点击显示全部图片" placement="top-end">
                  <el-image
                    fit="cover"
                    style="width: 300px"
                    :src="temp.task.images[0]"
                    :preview-src-list="temp.task.images"
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
import {
  getCancelApplyRecord,
  getCertification
} from "@/api/task_cancel_apply";
import waves from "@/directive/waves"; 
import { parseTime, deepClone } from "@/utils";
import Pagination from "@/components/Pagination"; 
import { handleCancelTask } from "@/api/task";

const StatusOptions = [
  { key: "PENDING", display_name: "等待处理" },
  { key: "QUICK_DEAL", display_name: "急需处理" },
  { key: "COMPLETE", display_name: "完成处理" }
];

// arr to obj, such as { PENDING : "等待处理", QUICK_DEAL : "急需处理" }
const StatusTypeKeyValue = StatusOptions.reduce((acc, cur) => {
  acc[cur.key] = cur.display_name;
  return acc;
}, {});

const taskCertification = {
  content: "",
  images: [],
  createTime: ""
};

const taskHandleRecord = {
  reason: '',
  taskId: '',
  type: 'refund'
}

export default {
  name: "TaskCancelApply",
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        COMPLETE: "success",
        PENDING: "info",
        QUICK_DEAL: "danger"
      };
      return statusMap[status] || "info";
    },
    typeFilter(type) {
      return StatusTypeKeyValue[type];
    },
    categoryFilter(id) {
      return StatusTypeKeyValue[id];
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
      taskCertification,
      taskHandleRecord,
      listQuery: {
        page: 1,
        limit: 20,
        taskId: undefined,
        status: undefined,
        minDate: undefined,
        maxDate: undefined,
        sort: "-id"
      },
      dateBound: "",
      visible: {},
      temp: {
        id: "",
        content: "",
        images: null,
        task: {
          options: [],
          images: []
        }
      },
      StatusOptions,
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
      pickerOptions: {
        shortcuts: [
          {
            text: "最近一周",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            }
          },
          {
            text: "最近一个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            }
          },
          {
            text: "最近三个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            }
          }
        ]
      },
      pvData: [],
      rules: {
        type: [
          { required: true, message: "type is required", trigger: "change" }
        ],
        timestamp: [
          {
            type: "date",
            required: true,
            message: "timestamp is required",
            trigger: "change"
          }
        ],
        title: [
          { required: true, message: "title is required", trigger: "blur" }
        ]
      },
      downloadLoading: false
    };
  },
  watch: {
    "listQuery.taskId"(newValue, oldValue) {
      if (+newValue instanceof Number) {
        return newValue;
      } else return oldValue;
    },
    dateBound(newValue) {
      if (newValue) {
        this.listQuery.minDate = parseTime(newValue[0]);
        this.listQuery.maxDate = parseTime(newValue[1]);
      } else {
        this.listQuery.minDate = null;
        this.listQuery.maxDate = null;
      }
      return newValue;
    }
  },
  created() {
    this.getList();
  },
  methods: {
    async getList() {
      this.listLoading = true;
      console.log(this.listQuery);
      const res = await getCancelApplyRecord(this.listQuery);
      console.log(res);
      const pageInfo = res.data;
      this.list = pageInfo.list;
      this.total = pageInfo.total;
      setTimeout(() => {
        this.listLoading = false;
      }, 1 * 1000);
    },
    async getCertification(taskId) {
      const res = await getCertification(taskId);
      const taskCertification = res.data;
      if (taskCertification) {
        taskCertification.images =
          taskCertification.images && JSON.parse(taskCertification.images);
        this.handleImg(taskCertification.images);
        console.log(taskCertification);
      }
      this.taskCertification = taskCertification;
    },
    handleCancel(row, $index) {
      this.taskHandleRecord.taskId = row.taskId;
      this.taskHandleRecord.reason = '';
      this.$nextTick(() => {
        this.visible.cur = $index
      })
    },
    async handleCancelTask(row, $index) {
      this.visible[$index] = false
      await handleCancelTask(this.taskHandleRecord)
      row.status = "COMPLETE";
      this.$notify({
              title: "Success",
              message: "Cancel Successfully",
              type: "success",
              duration: 2000
            });
    },
    handleFilter() {
      this.listQuery.page = 1;
      this.getList();
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
    handleCreate() {
      this.resetTemp();
      this.dialogStatus = "create";
      this.dialogFormVisible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate();
      });
    },
    handleUpdate(row) {
      this.temp = deepClone(row); // copy obj
      this.dialogStatus = "INFO";
      this.dialogFormVisible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate();
      });
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
    handleShow(row) {
      const temp = deepClone(row); // copy obj
      temp.images = JSON.parse(temp.images);
      this.handleImg(temp.images);
      const img_index = temp.task.wechatOptions.findIndex(
        option => option.type === "IMAGES"
      );
      if (img_index !== -1) {
        temp.task.images = temp.task.wechatOptions.splice(img_index, 1);
      }
      if (temp.task.images instanceof Array) {
        temp.task.images = temp.task.images.map(cur => cur.value);
      }
      console.log(temp.task.images);
      this.handleImg(temp.task.images);
      temp.task.options = temp.task.wechatOptions;
      this.getCertification(temp.taskId).then(res => {
        this.temp = temp;
        console.log(temp);
        console.log(res);
        this.dialogFormVisible = true;
        this.$nextTick(() => {
          this.$refs["dataForm"].clearValidate();
        });
      });
    },
    getSortClass: function(key) {
      const sort = this.listQuery.sort;
      return sort === `+${key}` ? "ascending" : "descending";
    }
  }
};
</script>
<style lang="scss">
.task_cancel_dialog {
  input,
  textarea {
    background: none !important;
  }
}
.tip-show {
  display: block !important;
}
.app-container section {
  display: block;
  box-sizing: border-box;
  padding: 15px 30px;
  margin-bottom: 30px;
  box-shadow: 1px 1px 4px #ccc;
}
section h2 {
  border-bottom: 1px solid #ccc;
  padding-bottom: 10px;
  margin-bottom: 40px;
}
</style>
