<div align="center">

<a href="http://marsview.cc/"><img src="https://marsview.cdn.bcebos.com/mars-logo.png" width="150" alt="marsview logo"></a>

# Marsview 低代码平台【社区版Java后台】

让搭建更简单，让开发更高效

</div>

## 介绍 🚀

Marsview 是一款中后台方向的低代码可视化搭建平台，开发者可以在平台上创建项目、页面和组件，支持事件交互、接口调用、数据联动和逻辑编排等，开发者还可通过微前端框架 microApp 快速集成到自己的业务系统中。

## 在线使用 🛸

- 🌍 [Marsview](http://www.marsview.cc/)

|                                                                        |                                                                        |
| ---------------------------------------------------------------------- | ---------------------------------------------------------------------- |
| ![Demo](https://imgcloud.cdn.bcebos.com/09d56ca14e47f7880d67bae37.png) | ![Demo](https://imgcloud.cdn.bcebos.com/09d56ca14e47f7880d67bae38.png) |
| ![Demo](https://imgcloud.cdn.bcebos.com/09d56ca14e47f7880d67bae39.png) | ![Demo](https://imgcloud.cdn.bcebos.com/09d56ca14e47f7880d67bae3a.png) |
| ![Demo](https://imgcloud.cdn.bcebos.com/09d56ca14e47f7880d67bae3b.png) | ![Demo](https://imgcloud.cdn.bcebos.com/09d56ca14e47f7880d67bae3c.png) |
| ![Demo](https://imgcloud.cdn.bcebos.com/09d56ca14e47f7880d67bae3d.png) | ![Demo](https://imgcloud.cdn.bcebos.com/09d56ca14e47f7880d67bae3e.png) |

## 特色 💥

- **项目：**
  项目配置（主题色、菜单布局、系统 Logo、面包屑...）、完整的 RBAC 的权限管理。
- **页面：** 页面创建、页面主题、页面配置、组件拖拽、样式配置、事件流配置、逻辑编排、接口配置。
  页面支持通过微服务的方式集成到传统项目中，无论你是 Vue 还是 React。
- **权限：** 项目和页面支持开发者和访问者权限配置，项目还支持菜单、按钮级别的 RBAC 控制。
- **自定义组件：** 当平台提供的组件满足不了需求时，可以自定义开发业务组件，平台会在线编译，上传到云端，同时在编辑器界面自定义组件中可以点击进行加载。
- **接口：** 接口统一管理，全局拦截器、返回结构修改等。支持 GET、POST、PUT、DELETE 等请求方式，支持接口动态参数传递。
- **事件流：** 通过事件流可以完成高难度的业务逻辑编排，比如：组件联动、组件显隐、组件禁用、自定义逻辑处理、接口调用、路由跳转等。
- **环境：** 平台支持三套环境，STG、PRE 和 PRD，页面只有发布到对应环境后，才可以在用户端访问到该页面。
- **回滚：** 平台发布后的页面支持一键回滚。
- **微服务：** 如果你是传统的 Vue 项目，想使用此平台，可以先在平台搭建一个页面发布到 PRD 环境，最后通过微服务集成进来。
- 后端提供 JAVA(本仓库，属于社区支撑) 和 Nodejs（官方支撑） 两个版本，数据库为 Mysql。

## 本地开发 👨‍💻

### 前端仓库

```bash
# 克隆代码
git clone https://github.com/JackySoft/marsview.git
```
### java代码仓库
```bash
# 由于国内网络原因，代码以gitee平台为主
git clone https://gitee.com/yangshare/marsview4j.git
```
