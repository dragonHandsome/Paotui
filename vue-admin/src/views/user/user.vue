<template>
  <div class="app-container">
    <el-button type="primary" @click="handleAddUser">New User</el-button>

    <el-table :data="usersList" style="width: 100%;margin-top:30px;" border>
      <el-table-column align="center" label="User Key" width="220">
        <template slot-scope="scope">{{ scope.row.id }}</template>
      </el-table-column>
      <el-table-column align="center" label="User Name" width="220">
        <template slot-scope="scope">{{ scope.row.username }}</template>
      </el-table-column>
      <el-table-column align="center" label="name">
        <template slot-scope="scope">{{ scope.row.name }}</template>
      </el-table-column>
      <el-table-column align="center" label="email">
        <template slot-scope="scope">{{ scope.row.email }}</template>
      </el-table-column>
      <el-table-column align="center" label="avatar">
        <template slot-scope="scope">
          <el-image
            style="width: 50px; height: 50px"
            :src="handleImg(scope.row.avatar)"
            fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="Operations">
        <template slot-scope="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope)">Edit</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :visible.sync="dialogVisible" :title="dialogType==='edit'?'Edit User':'New User'">
      <el-form :model="user" label-width="80px" label-position="left">
        <el-form-item label="username">
          <el-input v-model="user.username" placeholder="User Name" />
        </el-form-item>
        <el-form-item label="password">
          <template v-if="editPwd">
            <el-input v-model="user.password" type="password" />
            <el-button
              class="cancel-btn"
              size="small"
              icon="el-icon-refresh"
              type="warning"
              @click="(user.password = null) || (editPwd = false)"
            >cancel update</el-button>
          </template>
          <el-input v-else v-model="user.password" disabled type="password" />
          <template>
            <el-button
              v-if="editPwd"
              type="success"
              size="small"
              icon="el-icon-circle-check-outline"
              @click="editPwd = !editPwd"
            >Ok</el-button>
            <el-button
              v-else
              type="primary"
              size="small"
              icon="el-icon-edit"
              @click="(editPwd = !editPwd)"
            >Edit</el-button>
          </template>
        </el-form-item>
        <el-form-item label="name">
          <el-input v-model="user.name" placeholder="Name" />
        </el-form-item>
        <el-form-item label="email">
          <el-input v-model="user.email" placeholder="email" />
        </el-form-item>
        <el-form-item label="avatar">
          <el-upload
            style="float: left"
            drag
            :headers="{Authorization: 'Bearer ' + token}"
            :action="uploadURL"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            auto-upload
          >
            <i class="el-icon-upload" />
            <div class="el-upload__text">
              将文件拖到此处，或
              <em>点击上传</em>
            </div>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过1MB</div>
          </el-upload>
          <el-image
            v-if="user.avatar"
            style="width: 150px; height: 150px; float: left;margin-left: 20px"
            :src="handleImg(user.avatar)"
            fit="cover"
          />
        </el-form-item>
        <el-form-item label="Roles">
          <el-tree
            ref="tree"
            :check-strictly="checkStrictly"
            :data="rolesData"
            :props="defaultProps"
            accordion
            show-checkbox
            node-key="id"
            class="permission-tree"
          />
        </el-form-item>
      </el-form>
      <div style="text-align:right;">
        <el-button @click="dialogVisible=false">Cancel</el-button>
        <el-button type="primary" @click="confirmUser">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// import path from "path";
import { deepClone } from "@/utils";
import { getToken } from "@/utils/auth";
import { getRoles } from "@/api/role";
import { getUsers, addUser, deleteUser, updateUser } from "@/api/user";

const uploadURL = process.env.VUE_APP_BASE_API + "/adminPage/uploadFile";
const BASE_URL = process.env.VUE_APP_BASE_API;
const defaultUser = {
  id: "",
  username: "",
  name: "",
  email: "",
  avatar: "",
  password: null,
  roles: []
};

import store from '@/store'


console.dir(store)

import { connect, send } from '@/utils/websocket'

connect(store)

window.send = (content, type) => {
  let msg = {
    content,
    type: type || 'TO_USER',
    toUserId: 30
  }
  msg = JSON.stringify(msg)
  send(msg)
}

export default {
  data() {
    return {
      user: Object.assign({}, defaultUser),
      roles: [],
      editPwd: false,
      BASE_URL,
      usersList: [],
      uploadURL,
      dialogVisible: false,
      dialogType: "new",
      checkStrictly: true,
      token: getToken(),
      defaultProps: {
        children: "children",
        label: "name"
      }
    };
  },
  computed: {
    rolesData() {
      return this.roles;
    }
  },
  created() {
    // Mock: get all roles and users list from server
    this.getRoles();
    this.getUsers();
  },
  methods: {
    async getRoles() {
      const res = await getRoles();
      this.serviceRoles = res.data;
      this.roles = this.generateRoles(res.data);
    },
    async getUsers() {
      const res = await getUsers();
      this.usersList = res.data;
    },
    generateRoles(roles, disabled = false) {
      const res = [];
      const isRole = !disabled;
      for (const role of roles) {
        const data = {
          ...role,
          disabled
        };
        if (data.roleName) data.name = data.roleName;
        data.name = (isRole ? "ROLE_" : "MENU_") + data.name;
        res.push(data);
        const children = role.routes || role.children;
        if (children && children.length) {
          data.children = this.generateRoles(children, true);
        }
      }
      return res;
    },
    generateArr(roles) {
      const data = [];
      roles.forEach(role => {
        data.push(role);
      });
      return data;
    },
    handleAvatarSuccess(res, file) {
      if (res.errCode === 0) {
        this.user.avatar = res.data;
        this.$message("上传成功");
      } else {
        this.$message("上传失败");
      }
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === "image/jpeg";
      const isPNG = file.type === "image/png";
      const isLt1M = file.size / 1024 / 1024 < 1;

      if (!isJPG && !isPNG) {
        this.$message.error("上传头像图片只能是 JPG|PNG 格式!");
      }
      if (!isLt1M) {
        this.$message.error("上传头像图片大小不能超过 1MB!");
      }
      return (isPNG || isJPG) && isLt1M;
    },
    handleAddUser() {
      this.user = Object.assign({}, defaultUser);
      if (this.$refs.tree) {
        this.$refs.tree.setCheckedNodes([]);
      }
      this.dialogType = "new";
      this.editPwd = true;
      this.dialogVisible = true;
    },
    handleImg(img) {
      if (img.startsWith("http")) return img;
      return process.env.VUE_APP_BASE_API + img.replace("{root}", '');
    },
    handleEdit(scope) {
      this.dialogType = "edit";
      this.editPwd = false
      this.dialogVisible = true;
      this.user = deepClone(scope.row);
      this.user.password = null;
      this.$nextTick(() => {
        const roles = this.generateRoles(this.user.roles);
        this.$refs.tree.setCheckedNodes(this.generateArr(roles));
      });
    },
    handleDelete({ $index, row }) {
      this.$confirm("确定要删除此用户吗?", "Warning", {
        confirmButtonText: "Confirm",
        cancelButtonText: "Cancel",
        type: "warning"
      })
        .then(async () => {
          await deleteUser(row.id);
          this.usersList.splice($index, 1);
          this.$message({
            type: "success",
            message: "Delete succed!"
          });
        })
        .catch(err => {
          console.error(err);
        });
    },
    generateTree(roles, checkedKeys) {
      const res = [];
      for (const role of roles) {
        if (checkedKeys.some(id => id === role.id)) {
          res.push(role);
        }
      }
      return res;
    },
    async confirmUser() {
      const isEdit = this.dialogType === "edit";
      const checkedKeys = this.$refs.tree.getCheckedKeys();
      this.user.roles = this.generateTree(
        deepClone(this.serviceRoles),
        checkedKeys
      );
      if (isEdit) {
        await updateUser(this.user);
        for (let index = 0; index < this.usersList.length; index++) {
          if (this.usersList[index].id === this.user.id) {
            this.usersList.splice(index, 1, Object.assign({}, this.user));
            break;
          }
        }
      } else {
        const { data } = await addUser(this.user);
        this.user.id = data.id;
        this.usersList.push(this.user);
      }

      const { email, id, username } = this.user;
      this.dialogVisible = false;
      this.$notify({
        title: "Success",
        dangerouslyUseHTMLString: true,
        message: `
            <div>User Key: ${id}</div>
            <div>User Name: ${username}</div>
            <div>email: ${email}</div>
          `,
        type: "success"
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.app-container {
  .users-table {
    margin-top: 30px;
  }
  .permission-tree {
    margin-bottom: 30px;
  }
}
</style>
