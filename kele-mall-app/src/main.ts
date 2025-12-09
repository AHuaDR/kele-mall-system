import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'
import 'vant/lib/index.css'
import App from './App.vue'
import router from './router'

import { 
  Tabbar, TabbarItem, Search, Swipe, SwipeItem, Icon, Sticky, CountDown, PullRefresh,
  Button, Card, Checkbox, Stepper, SubmitBar, SwipeCell, 
  NavBar, Form, Field, CellGroup, Cell, Image as VanImage, 
  TreeSelect, 
  ActionBar, ActionBarIcon, ActionBarButton, 
  ActionSheet, 
  Tab, Tabs, 
  AddressList, AddressEdit, 
  Tag, 
  CouponList, CouponCell,
  DropdownMenu, DropdownItem,
  Popup, List, Switch,
  Picker, DatePicker,
  Uploader
} from 'vant'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// 注册所有组件
app.use(Tabbar).use(TabbarItem).use(Search).use(Swipe).use(SwipeItem).use(Icon).use(Sticky).use(CountDown).use(PullRefresh)
app.use(Button).use(Card).use(Checkbox).use(Stepper).use(SubmitBar).use(SwipeCell)
app.use(NavBar).use(Form).use(Field).use(CellGroup).use(Cell).use(VanImage)
app.use(TreeSelect)
app.use(ActionBar).use(ActionBarIcon).use(ActionBarButton).use(ActionSheet).use(Tag)
app.use(Tab).use(Tabs)
app.use(AddressList).use(AddressEdit).use(CouponList).use(CouponCell)
app.use(DropdownMenu).use(DropdownItem)
app.use(Popup).use(List).use(Switch)
app.use(Picker).use(DatePicker)
app.use(Uploader)

app.mount('#app')