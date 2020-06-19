import request from '@/utils/request'

export function getCancelApplyRecord(Query) {
    return request.GET({
        url: '/adminPage/task/cancelApplyRecord',
        data: Query
    })
}

export function getCertification(taskId) {
    return request.GET({
        url: `/adminPage/task/certification/${taskId}`
    })
}

export function handleTask(data) {
    return request.POST({
        url: `/adminPage/task`,
        data
    })
}
