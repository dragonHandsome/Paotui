<template>
  <div class="app-container">
    <el-button type="primary" @click="handleAddMenu">New menu</el-button>

    <el-table :data="serviceMenu" style="width: 100%;margin-top:30px;" border>
      <el-table-column align="center" label="menu key" width="220">
        <template slot-scope="scope">{{ scope.row.id }}</template>
      </el-table-column>
      <el-table-column align="center" label="menu title" width="220">
        <template slot-scope="scope">{{ scope.row.title }}</template>
      </el-table-column>
      <el-table-column align="center" label="menu path" width="320">
        <template slot-scope="scope">{{ scope.row.path }}</template>
      </el-table-column>
      <el-table-column align="center" label="menu reply" width="220">
        <template slot-scope="scope">{{ scope.row.rely }}</template>
      </el-table-column>
      <el-table-column align="center" label="menu icon" width="220">
        <template slot-scope="scope">
          <svg-icon :icon-class="scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="Operations">
        <template slot-scope="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope)">Edit</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :visible.sync="dialogVisible" :title="dialogType==='edit'?'Edit menu':'New menu'">
      <el-form :model="menu" label-width="80px" label-position="left">
        <el-form-item label="title">
          <el-input v-model="menu.title" placeholder="Menu title" />
        </el-form-item>
        <el-form-item label="path">
          <el-input v-model="menu.path" placeholder="Menu path" />
        </el-form-item>
        <el-form-item label="icon">
          <el-select v-model="menu.icon" placeholder="Select icon">
            <el-option v-for="item in svgIcons" :key="item" :label="item" :value="item">
              <svg-icon :icon-class="item" />
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="rely">
          <el-select v-model="menu.rely" placeholder="Menu rely">
            <el-option v-for="item in menuData" :key="item.id" :label="item.title" :value="item.id">
              <span style="float: left">{{ item.title }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.path }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="view">
          <el-select v-model="menu.component" placeholder="Menu component">
            <el-option v-for="item in views" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
      </el-form>
      <div style="text-align:right;">
        <el-button @click="dialogVisible=false">Cancel</el-button>
        <el-button type="primary" @click="confirm">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMenu, addMenu, updateMenu, deleteMenu } from '@/api/menu'
import svgIcons from './svg-icons'
import views from './view-generate'

const defaultMenu = {
  id: '',
  path: '',
  title: '',
  icon: '',
  rely: 0,
  component: ''
}

export default {
  data() {
    return {
      menu: Object.assign({}, defaultMenu),
      serviceMenu: [],
      dialogVisible: false,
      dialogType: 'new',
      svgIcons,
      views
    }
  },
  computed: {
    menuData() {
      return this.serviceMenu.filter(menu => menu.id !== this.menu.id)
    }
  },
  created() {
    this.getMenu()
  },
  methods: {
    async getMenu() {
      const res = await getMenu()
      this.serviceMenu = res.data
    },
    handleAddMenu() {
      this.menu = Object.assign({}, defaultMenu)
      this.dialogType = 'new'
      this.dialogVisible = true
    },
    handleEdit(scope) {
      this.dialogType = 'edit'
      this.dialogVisible = true
      this.menu = Object.assign({}, scope.row)
    },
    handleDelete({ $index, row }) {
      this.$confirm(`确定要删除[${row.title}]吗?`, 'Warning', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      })
        .then(async() => {
          await deleteMenu(row.id)
          this.serviceMenu = this.serviceMenu.filter(menu => menu.rely != row.id && menu.id != row.id)
          // this.serviceMenu.splice($index, 1)
          this.$message({
            type: 'success',
            message: 'Delete succed!'
          })
        })
        .catch(err => { console.error(err) })
    },
    async confirm() {
      const isEdit = this.dialogType === 'edit'
      if (isEdit) {
        await updateMenu(this.menu)
        for (let index = 0; index < this.serviceMenu.length; index++) {
          if (this.serviceMenu[index].id === this.menu.id) {
            this.serviceMenu.splice(index, 1, Object.assign({}, this.menu))
            break
          }
        }
      } else {
        const { data } = await addMenu(this.menu)
        this.menu.id = data.id
        this.serviceMenu.push(this.menu)
      }

      const { id, title, path } = this.menu
      this.dialogVisible = false
      this.$notify({
        title: 'Success',
        dangerouslyUseHTMLString: true,
        message: `
            <div>Role Key: ${id}</div>
            <div>Role Name: ${title}</div>
            <div>Description: ${path}</div>
          `,
        type: 'success'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  .roles-table {
    margin-top: 30px;
  }
  .permission-tree {
    margin-bottom: 30px;
  }
}
</style>
