const req = require.context('../../views', true, /\.vue$/)
const requireAll = requireContext => requireContext.keys()

const re = /\.\/(.*)\.vue/

const views = requireAll(req).map(i => {
  return i.match(re)[1]
})
views.unshift('@/layout')
export default views
