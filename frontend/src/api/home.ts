import request from './request'

/**
 * 首页API
 */

/**
 * 获取首页统计数据
 */
export function getHomeStatistics() {
  return request.get('/home/statistics')
}

