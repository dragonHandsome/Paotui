
import request from '@/utils/request'

export function getTasks(query) {
    return request.GET({
        url: 'adminPage/task',
        data: query
    })
}

export function getTaskDetail(id) {
    return request.GET({
        url: `adminPage/task/${id}`
    })
}

export function handleCancelTask(data) {
    return request.POST({
        url: `adminPage/task`,
        data
    })
}
/**
 * /adminPage/statistics
 countAllTaskGroupByCategories,
  countAllTaskGroupByLastWeekDayAndCategories,
  countAllTaskGroupByLastWeekDayAndHasrewards
 */
export function countAllTaskGroupByCategories() {
    return request.GET({
        url: `adminPage/statistics/countAllTaskGroupByCategories`
    })
}

export function countAllTaskGroupByLastWeekDayAndCategories() {
    return request.GET({
        url: `adminPage/statistics/countAllTaskGroupByLastWeekDayAndCategories`
    })
}

export function countAllTaskGroupByLastWeekDayAndHasrewards() {
    return request.GET({
        url: `adminPage/statistics/countAllTaskGroupByLastWeekDayAndHasrewards`
    })
}
