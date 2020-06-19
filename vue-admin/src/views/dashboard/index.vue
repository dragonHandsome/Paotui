<template>
  <div class="dashboard-editor-container">
    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <line-chart :chart-data="lineChartData" />
    </el-row>

    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <pie-chart :chart-data="PieChartData" />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="16">
        <div class="chart-wrapper">
          <bar-chart :chart-data="BarChartData" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import LineChart from "./components/LineChart";
import RaddarChart from "./components/RaddarChart";
import PieChart from "./components/PieChart";
import BarChart from "./components/BarChart";
import {
  countAllTaskGroupByCategories,
  countAllTaskGroupByLastWeekDayAndCategories,
  countAllTaskGroupByLastWeekDayAndHasrewards
} from "@/api/task";
import { deepClone } from "@/utils";

const dayWeek = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

const dayWeekData = dayWeek.reduce(
  (acc, cur) => {
    acc[cur] = acc.i++;
    return acc;
  },
  { i: 0 }
);

const categories = ["buy", "express", "issue", "library"];
const defaultCategoryGroup = categories.reduce((acc, cur) => {
  acc[cur] = [0, 0, 0, 0, 0, 0, 0];
  return acc;
}, {});

//#获取上周根据是否有钱和星期几的任务数
const hasRewardOrginData = [0, 0, 0, 0, 0, 0, 0];

const noRewardOrginData = [0, 0, 0, 0, 0, 0, 0];

const lineChartData = {
  title: "上周任务数",
  hasReward: [].concat(hasRewardOrginData),
  noReward: [].concat(noRewardOrginData)
};
//#获取上周根据种类和星期几的单数
const defaultSeriesItem = {
  name: "",
  type: "bar",
  stack: "vistors",
  barWidth: "60%",
  data: [],
  animationDuration: 6000
};
const defaultPieSeriesItem = {
  name: "任务总数",
  type: "pie",
  roseType: "radius",
  radius: [15, 95],
  center: ["50%", "38%"],
  data: [{ value: 320, name: "Industries" }],
  animationEasing: "cubicInOut",
  animationDuration: 2600
};
export default {
  name: "DashboardAdmin",
  components: {
    LineChart,
    RaddarChart,
    PieChart,
    BarChart
  },
  data() {
    return {
      lineChartData: deepClone(lineChartData),
      BarChartData: {
        series: null,
        title: "Group by category"
      },
      PieChartData: {
        series: null
      }
    };
  },
  created() {
    this.countAllTaskGroupByLastWeekDayAndHasrewards();
    this.countAllTaskGroupByLastWeekDayAndCategories();
    this.countAllTaskGroupByCategories();
  },
  methods: {
    /**
     * countItem: {
     *  count,
     *  category,
     *  date
     * }
     */
    async countAllTaskGroupByLastWeekDayAndCategories() {
      const res = await countAllTaskGroupByLastWeekDayAndCategories();
      const countList = res.data;
      const categoriesGroup = Object.assign({}, defaultCategoryGroup);
      countList.forEach(cur => {
        //获取星期几的索引
        const index = dayWeekData[cur.date];
        //根据种类分组
        if (!categoriesGroup[cur.category]) {
          categoriesGroup[cur.category] = [0, 0, 0, 0, 0, 0, 0];
        }
        categoriesGroup[cur.category][index] = cur.count;
      });
      const series = [];
      Object.keys(categoriesGroup).forEach(category => {
        const seriesItem = Object.assign({}, defaultSeriesItem);
        seriesItem.name = category;
        seriesItem.data = categoriesGroup[category];
        series.push(seriesItem);
      });
      //更新
      console.log("series");
      console.log(series);
      this.BarChartData.series = series;
    },
    async countAllTaskGroupByLastWeekDayAndHasrewards() {
      const res = await countAllTaskGroupByLastWeekDayAndHasrewards();
      const countList = res.data;
      const lineChartData = Object.assign({}, this.lineChartData);
      countList.forEach(cur => {
        //获取星期几的索引
        const index = dayWeekData[cur.date];
        if (cur.hasReward) {
          lineChartData.hasReward[index] = cur.count;
        } else {
          lineChartData.noReward[index] = cur.count;
        }
      });
      //更新视图
      this.lineChartData = lineChartData;
    },
    /**
     * {
     *  count,
     *  category
     * }
     */
    async countAllTaskGroupByCategories() {
      console.log("countAllTaskGroupByCategories");
      const res = await countAllTaskGroupByCategories();
      const countList = res.data;
      const seriesItem = Object.assign({}, defaultPieSeriesItem);
      const categoryGroup = Object.assign({}, defaultCategoryGroup);
      countList.forEach(cur => {
        //这里如果后台有新的 那也会加进来 种类
        categoryGroup[cur.category] = cur.count;
      });
      //后台如果种类多了也不怕
      const categoryList = Object.keys(categoryGroup);
      const series = [];
      const seriesItemData = categoryList.reduce((acc, key) => {
        acc.push({
          name: key,
          value: categoryGroup[key]
        });
        return acc;
      }, []);
      seriesItem.data = seriesItemData;
      series.push(seriesItem);
      this.PieChartData.series = series;
      this.PieChartData.titles = categoryList;
    }
  }
};
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

@media (max-width: 1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
