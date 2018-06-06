/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50536
Source Host           : localhost:3306
Source Database       : cailiao

Target Server Type    : MYSQL
Target Server Version : 50536
File Encoding         : 65001

Date: 2016-11-07 12:30:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for cms_article
-- ----------------------------
DROP TABLE IF EXISTS `cms_article`;
CREATE TABLE `cms_article` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `category_id` varchar(64) NOT NULL COMMENT '栏目编号',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `link` varchar(255) DEFAULT NULL COMMENT '文章链接',
  `color` varchar(50) DEFAULT NULL COMMENT '标题颜色',
  `image` varchar(255) DEFAULT NULL COMMENT '文章图片',
  `keywords` varchar(255) DEFAULT NULL COMMENT '关键字',
  `description` varchar(255) DEFAULT NULL COMMENT '描述、摘要',
  `weight` int(11) DEFAULT '0' COMMENT '权重，越大越靠前',
  `weight_date` datetime DEFAULT NULL COMMENT '权重期限',
  `hits` int(11) DEFAULT '0' COMMENT '点击数',
  `posid` varchar(10) DEFAULT NULL COMMENT '推荐位，多选',
  `custom_content_view` varchar(255) DEFAULT NULL COMMENT '自定义内容视图',
  `view_config` text COMMENT '视图配置',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `cms_article_create_by` (`create_by`),
  KEY `cms_article_title` (`title`),
  KEY `cms_article_keywords` (`keywords`),
  KEY `cms_article_del_flag` (`del_flag`),
  KEY `cms_article_weight` (`weight`),
  KEY `cms_article_update_date` (`update_date`),
  KEY `cms_article_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文章表';

-- ----------------------------
-- Records of cms_article
-- ----------------------------

-- ----------------------------
-- Table structure for cms_article_data
-- ----------------------------
DROP TABLE IF EXISTS `cms_article_data`;
CREATE TABLE `cms_article_data` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `content` text COMMENT '文章内容',
  `copyfrom` varchar(255) DEFAULT NULL COMMENT '文章来源',
  `relation` varchar(255) DEFAULT NULL COMMENT '相关文章',
  `allow_comment` char(1) DEFAULT NULL COMMENT '是否允许评论',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文章详表';

-- ----------------------------
-- Records of cms_article_data
-- ----------------------------

-- ----------------------------
-- Table structure for cms_category
-- ----------------------------
DROP TABLE IF EXISTS `cms_category`;
CREATE TABLE `cms_category` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `parent_id` varchar(64) NOT NULL COMMENT '父级编号',
  `parent_ids` varchar(2000) NOT NULL COMMENT '所有父级编号',
  `site_id` varchar(64) DEFAULT '1' COMMENT '站点编号',
  `office_id` varchar(64) DEFAULT NULL COMMENT '归属机构',
  `module` varchar(20) DEFAULT NULL COMMENT '栏目模块',
  `name` varchar(100) NOT NULL COMMENT '栏目名称',
  `image` varchar(255) DEFAULT NULL COMMENT '栏目图片',
  `href` varchar(255) DEFAULT NULL COMMENT '链接',
  `target` varchar(20) DEFAULT NULL COMMENT '目标',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `keywords` varchar(255) DEFAULT NULL COMMENT '关键字',
  `sort` int(11) DEFAULT '30' COMMENT '排序（升序）',
  `in_menu` char(1) DEFAULT '1' COMMENT '是否在导航中显示',
  `in_list` char(1) DEFAULT '1' COMMENT '是否在分类页中显示列表',
  `show_modes` char(1) DEFAULT '0' COMMENT '展现方式',
  `allow_comment` char(1) DEFAULT NULL COMMENT '是否允许评论',
  `is_audit` char(1) DEFAULT NULL COMMENT '是否需要审核',
  `custom_list_view` varchar(255) DEFAULT NULL COMMENT '自定义列表视图',
  `custom_content_view` varchar(255) DEFAULT NULL COMMENT '自定义内容视图',
  `view_config` text COMMENT '视图配置',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `cms_category_parent_id` (`parent_id`),
  KEY `cms_category_module` (`module`),
  KEY `cms_category_name` (`name`),
  KEY `cms_category_sort` (`sort`),
  KEY `cms_category_del_flag` (`del_flag`),
  KEY `cms_category_office_id` (`office_id`),
  KEY `cms_category_site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='栏目表';

-- ----------------------------
-- Records of cms_category
-- ----------------------------
INSERT INTO `cms_category` VALUES ('1', '0', '0,', '0', '54d05cbfef4f4bafb39ec67d0d26211c', '', '顶级栏目', '', '', '', '', '', '0', '1', '1', '0', '0', '1', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('10', '1', '0,1,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '软件介绍', '', '', '', '', '', '20', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('11', '10', '0,1,10,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '网络工具', '', '', '', '', '', '30', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('12', '10', '0,1,10,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '浏览工具', '', '', '', '', '', '40', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('13', '10', '0,1,10,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '浏览辅助', '', '', '', '', '', '50', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('14', '10', '0,1,10,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '网络优化', '', '', '', '', '', '50', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('15', '10', '0,1,10,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '邮件处理', '', '', '', '', '', '50', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('16', '10', '0,1,10,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '下载工具', '', '', '', '', '', '50', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('17', '10', '0,1,10,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '搜索工具', '', '', '', '', '', '50', '1', '1', '2', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('18', '1', '0,1,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'link', '友情链接', '', '', '', '', '', '90', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('19', '18', '0,1,18,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'link', '常用网站', '', '', '', '', '', '50', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('1c67130c89b94d058acb4d1ff98e5322', '1', '0,1,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', '', 'ss', '/iLife/userfiles/1/_thumbs/images/cms/category/2016/11/Chrysanthemum.jpg', '', '', '', '', '30', '0', '1', '0', '0', '0', '', '', '', '1', '2016-11-03 14:50:01', '1', '2016-11-03 14:50:01', null, '0');
INSERT INTO `cms_category` VALUES ('2', '1', '0,1,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '组织机构', '', '', '', '', '', '10', '1', '1', '0', '0', '1', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('20', '18', '0,1,18,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'link', '门户网站', '', '', '', '', '', '50', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('21', '18', '0,1,18,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'link', '购物网站', '', '', '', '', '', '50', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('22', '18', '0,1,18,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'link', '交友社区', '', '', '', '', '', '50', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('23', '18', '0,1,18,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'link', '音乐视频', '', '', '', '', '', '50', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('24', '1', '0,1,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', '', '百度一下', '', 'http://www.baidu.com', '_blank', '', '', '90', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('25', '1', '0,1,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', '', '全文检索', '', '/search', '', '', '', '90', '0', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('26', '1', '0,1,', '2', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '测试栏目', '', '', '', '', '', '90', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('27', '1', '0,1,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', '', '公共留言', '', '/guestbook', '', '', '', '90', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('3', '2', '0,1,2,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '网站简介', '', '', '', '', '', '30', '1', '1', '0', '0', '1', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('4', '2', '0,1,2,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '内部机构', '', '', '', '', '', '40', '1', '1', '0', '0', '1', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('5', '2', '0,1,2,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '地方机构', '', '', '', '', '', '50', '1', '1', '0', '0', '1', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('6', '1', '0,1,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '质量检验', '', '', '', '', '', '20', '1', '1', '1', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('7', '6', '0,1,6,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '产品质量', '', '', '', '', '', '30', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('8', '6', '0,1,6,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '技术质量', '', '', '', '', '', '40', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_category` VALUES ('9', '6', '0,1,6,', '1', '54d05cbfef4f4bafb39ec67d0d26211c', 'article', '工程质量', '', '', '', '', '', '50', '1', '1', '0', '1', '0', null, null, null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');

-- ----------------------------
-- Table structure for cms_comment
-- ----------------------------
DROP TABLE IF EXISTS `cms_comment`;
CREATE TABLE `cms_comment` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `category_id` varchar(64) NOT NULL COMMENT '栏目编号',
  `content_id` varchar(64) NOT NULL COMMENT '栏目内容的编号',
  `title` varchar(255) DEFAULT NULL COMMENT '栏目内容的标题',
  `content` varchar(255) DEFAULT NULL COMMENT '评论内容',
  `name` varchar(100) DEFAULT NULL COMMENT '评论姓名',
  `ip` varchar(100) DEFAULT NULL COMMENT '评论IP',
  `create_date` datetime NOT NULL COMMENT '评论时间',
  `audit_user_id` varchar(64) DEFAULT NULL COMMENT '审核人',
  `audit_date` datetime DEFAULT NULL COMMENT '审核时间',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `cms_comment_category_id` (`category_id`),
  KEY `cms_comment_content_id` (`content_id`),
  KEY `cms_comment_status` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='评论表';

-- ----------------------------
-- Records of cms_comment
-- ----------------------------

-- ----------------------------
-- Table structure for cms_guestbook
-- ----------------------------
DROP TABLE IF EXISTS `cms_guestbook`;
CREATE TABLE `cms_guestbook` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `type` char(1) NOT NULL COMMENT '留言分类',
  `content` varchar(255) NOT NULL COMMENT '留言内容',
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `phone` varchar(100) NOT NULL COMMENT '电话',
  `workunit` varchar(100) NOT NULL COMMENT '单位',
  `ip` varchar(100) NOT NULL COMMENT 'IP',
  `create_date` datetime NOT NULL COMMENT '留言时间',
  `re_user_id` varchar(64) DEFAULT NULL COMMENT '回复人',
  `re_date` datetime DEFAULT NULL COMMENT '回复时间',
  `re_content` varchar(100) DEFAULT NULL COMMENT '回复内容',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `cms_guestbook_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='留言板';

-- ----------------------------
-- Records of cms_guestbook
-- ----------------------------

-- ----------------------------
-- Table structure for cms_link
-- ----------------------------
DROP TABLE IF EXISTS `cms_link`;
CREATE TABLE `cms_link` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `category_id` varchar(64) NOT NULL COMMENT '栏目编号',
  `title` varchar(255) NOT NULL COMMENT '链接名称',
  `color` varchar(50) DEFAULT NULL COMMENT '标题颜色',
  `image` varchar(255) DEFAULT NULL COMMENT '链接图片',
  `href` varchar(255) DEFAULT NULL COMMENT '链接地址',
  `weight` int(11) DEFAULT '0' COMMENT '权重，越大越靠前',
  `weight_date` datetime DEFAULT NULL COMMENT '权重期限',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `cms_link_category_id` (`category_id`),
  KEY `cms_link_title` (`title`),
  KEY `cms_link_del_flag` (`del_flag`),
  KEY `cms_link_weight` (`weight`),
  KEY `cms_link_create_by` (`create_by`),
  KEY `cms_link_update_date` (`update_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='友情链接';

-- ----------------------------
-- Records of cms_link
-- ----------------------------

-- ----------------------------
-- Table structure for cms_site
-- ----------------------------
DROP TABLE IF EXISTS `cms_site`;
CREATE TABLE `cms_site` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '站点名称',
  `title` varchar(100) NOT NULL COMMENT '站点标题',
  `logo` varchar(255) DEFAULT NULL COMMENT '站点Logo',
  `domain` varchar(255) DEFAULT NULL COMMENT '站点域名',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `keywords` varchar(255) DEFAULT NULL COMMENT '关键字',
  `theme` varchar(255) DEFAULT 'default' COMMENT '主题',
  `copyright` text COMMENT '版权信息',
  `custom_index_view` varchar(255) DEFAULT NULL COMMENT '自定义站点首页视图',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `cms_site_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='站点表';

-- ----------------------------
-- Records of cms_site
-- ----------------------------
INSERT INTO `cms_site` VALUES ('1', '默认站点', 'iLife Web', null, null, 'iLife', 'iLife', 'basic', 'Copyright &copy; 2012-2013 <a href=\'http://thinkgem.iteye.com\' target=\'_blank\'>ThinkGem</a> - Powered By <a href=\'https://github.com/thinkgem/iLife\' target=\'_blank\'>iLife</a> V1.0', null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `cms_site` VALUES ('2', '子站点测试', 'iLife Subsite', null, null, 'iLife subsite', 'iLife subsite', 'basic', 'Copyright &copy; 2012-2013 <a href=\'http://thinkgem.iteye.com\' target=\'_blank\'>ThinkGem</a> - Powered By <a href=\'https://github.com/thinkgem/iLife\' target=\'_blank\'>iLife</a> V1.0', null, '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');

-- ----------------------------
-- Table structure for gen_scheme
-- ----------------------------
DROP TABLE IF EXISTS `gen_scheme`;
CREATE TABLE `gen_scheme` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `name` varchar(200) DEFAULT NULL COMMENT '名称',
  `category` varchar(2000) DEFAULT NULL COMMENT '分类',
  `package_name` varchar(500) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `sub_module_name` varchar(30) DEFAULT NULL COMMENT '生成子模块名',
  `function_name` varchar(500) DEFAULT NULL COMMENT '生成功能名',
  `function_name_simple` varchar(100) DEFAULT NULL COMMENT '生成功能名（简写）',
  `function_author` varchar(100) DEFAULT NULL COMMENT '生成功能作者',
  `gen_table_id` varchar(200) DEFAULT NULL COMMENT '生成表编号',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记（0：正常；1：删除）',
  PRIMARY KEY (`id`),
  KEY `gen_scheme_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='生成方案';

-- ----------------------------
-- Records of gen_scheme
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `name` varchar(200) DEFAULT NULL COMMENT '名称',
  `comments` varchar(500) DEFAULT NULL COMMENT '描述',
  `class_name` varchar(100) DEFAULT NULL COMMENT '实体类名称',
  `parent_table` varchar(200) DEFAULT NULL COMMENT '关联父表',
  `parent_table_fk` varchar(100) DEFAULT NULL COMMENT '关联父表外键',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记（0：正常；1：删除）',
  PRIMARY KEY (`id`),
  KEY `gen_table_name` (`name`),
  KEY `gen_table_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务表';

-- ----------------------------
-- Records of gen_table
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `gen_table_id` varchar(64) DEFAULT NULL COMMENT '归属表编号',
  `name` varchar(200) DEFAULT NULL COMMENT '名称',
  `comments` varchar(500) DEFAULT NULL COMMENT '描述',
  `jdbc_type` varchar(100) DEFAULT NULL COMMENT '列的数据类型的字节长度',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键',
  `is_null` char(1) DEFAULT NULL COMMENT '是否可为空',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段',
  `query_type` varchar(200) DEFAULT NULL COMMENT '查询方式（等于、不等于、大于、小于、范围、左LIKE、右LIKE、左右LIKE）',
  `show_type` varchar(200) DEFAULT NULL COMMENT '字段生成方案（文本框、文本域、下拉框、复选框、单选框、字典选择、人员选择、部门选择、区域选择）',
  `dict_type` varchar(200) DEFAULT NULL COMMENT '字典类型',
  `settings` varchar(2000) DEFAULT NULL COMMENT '其它设置（扩展字段JSON）',
  `sort` decimal(10,0) DEFAULT NULL COMMENT '排序（升序）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记（0：正常；1：删除）',
  PRIMARY KEY (`id`),
  KEY `gen_table_column_table_id` (`gen_table_id`),
  KEY `gen_table_column_name` (`name`),
  KEY `gen_table_column_sort` (`sort`),
  KEY `gen_table_column_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务表字段';

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------

-- ----------------------------
-- Table structure for gen_template
-- ----------------------------
DROP TABLE IF EXISTS `gen_template`;
CREATE TABLE `gen_template` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `name` varchar(200) DEFAULT NULL COMMENT '名称',
  `category` varchar(2000) DEFAULT NULL COMMENT '分类',
  `file_path` varchar(500) DEFAULT NULL COMMENT '生成文件路径',
  `file_name` varchar(200) DEFAULT NULL COMMENT '生成文件名',
  `content` text COMMENT '内容',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记（0：正常；1：删除）',
  PRIMARY KEY (`id`),
  KEY `gen_template_del_falg` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代码模板表';

-- ----------------------------
-- Records of gen_template
-- ----------------------------

-- ----------------------------
-- Table structure for oa_leave
-- ----------------------------
DROP TABLE IF EXISTS `oa_leave`;
CREATE TABLE `oa_leave` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `process_instance_id` varchar(64) DEFAULT NULL COMMENT '流程实例编号',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `leave_type` varchar(20) DEFAULT NULL COMMENT '请假类型',
  `reason` varchar(255) DEFAULT NULL COMMENT '请假理由',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `reality_start_time` datetime DEFAULT NULL COMMENT '实际开始时间',
  `reality_end_time` datetime DEFAULT NULL COMMENT '实际结束时间',
  `create_by` varchar(64) NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `oa_leave_create_by` (`create_by`),
  KEY `oa_leave_process_instance_id` (`process_instance_id`),
  KEY `oa_leave_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='请假流程表';

-- ----------------------------
-- Records of oa_leave
-- ----------------------------

-- ----------------------------
-- Table structure for oa_notify
-- ----------------------------
DROP TABLE IF EXISTS `oa_notify`;
CREATE TABLE `oa_notify` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `type` char(1) DEFAULT NULL COMMENT '类型',
  `title` varchar(200) DEFAULT NULL COMMENT '标题',
  `content` varchar(2000) DEFAULT NULL COMMENT '内容',
  `files` varchar(2000) DEFAULT NULL COMMENT '附件',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `create_by` varchar(64) NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `oa_notify_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='通知通告';

-- ----------------------------
-- Records of oa_notify
-- ----------------------------

-- ----------------------------
-- Table structure for oa_notify_record
-- ----------------------------
DROP TABLE IF EXISTS `oa_notify_record`;
CREATE TABLE `oa_notify_record` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `oa_notify_id` varchar(64) DEFAULT NULL COMMENT '通知通告ID',
  `user_id` varchar(64) DEFAULT NULL COMMENT '接受人',
  `read_flag` char(1) DEFAULT '0' COMMENT '阅读标记',
  `read_date` date DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  KEY `oa_notify_record_notify_id` (`oa_notify_id`),
  KEY `oa_notify_record_user_id` (`user_id`),
  KEY `oa_notify_record_read_flag` (`read_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='通知通告发送记录';

-- ----------------------------
-- Records of oa_notify_record
-- ----------------------------

-- ----------------------------
-- Table structure for oa_test_audit
-- ----------------------------
DROP TABLE IF EXISTS `oa_test_audit`;
CREATE TABLE `oa_test_audit` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `PROC_INS_ID` varchar(64) DEFAULT NULL COMMENT '流程实例ID',
  `USER_ID` varchar(64) DEFAULT NULL COMMENT '变动用户',
  `OFFICE_ID` varchar(64) DEFAULT NULL COMMENT '归属部门',
  `POST` varchar(255) DEFAULT NULL COMMENT '岗位',
  `AGE` char(1) DEFAULT NULL COMMENT '性别',
  `EDU` varchar(255) DEFAULT NULL COMMENT '学历',
  `CONTENT` varchar(255) DEFAULT NULL COMMENT '调整原因',
  `OLDA` varchar(255) DEFAULT NULL COMMENT '现行标准 薪酬档级',
  `OLDB` varchar(255) DEFAULT NULL COMMENT '现行标准 月工资额',
  `OLDC` varchar(255) DEFAULT NULL COMMENT '现行标准 年薪总额',
  `NEWA` varchar(255) DEFAULT NULL COMMENT '调整后标准 薪酬档级',
  `NEWB` varchar(255) DEFAULT NULL COMMENT '调整后标准 月工资额',
  `NEWC` varchar(255) DEFAULT NULL COMMENT '调整后标准 年薪总额',
  `ADD_NUM` varchar(255) DEFAULT NULL COMMENT '月增资',
  `EXE_DATE` varchar(255) DEFAULT NULL COMMENT '执行时间',
  `HR_TEXT` varchar(255) DEFAULT NULL COMMENT '人力资源部门意见',
  `LEAD_TEXT` varchar(255) DEFAULT NULL COMMENT '分管领导意见',
  `MAIN_LEAD_TEXT` varchar(255) DEFAULT NULL COMMENT '集团主要领导意见',
  `create_by` varchar(64) NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `OA_TEST_AUDIT_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='审批流程测试表';

-- ----------------------------
-- Records of oa_test_audit
-- ----------------------------
INSERT INTO `oa_test_audit` VALUES ('1613cb512e514478879c5d5e79d28bbc', 'e6644f9e3d834ae294821ee57e15e6cc', '8', '10', '', null, null, '表现优秀', '2', '2000', '1000', '5', '5000', '5000', '3000', '', '[同意] qq', '[同意] 22', '[同意] ee', '1', '2016-11-03 14:44:07', '1', '2016-11-03 16:56:39', null, '0');
INSERT INTO `oa_test_audit` VALUES ('1caeef63c8e24c44a87652fa3d6ae725', '4b348b811d8249d0973b25b02b82eeae', '295def53ef614f06b164f78c4944eb4d', '56c6fb3511a548c09fb0964b00b2145c', '', null, null, '1', '1', '3', '5', '2', '4', '6', '1', '2', '[驳回] 213', null, null, '1', '2016-11-03 17:28:12', '1', '2016-11-03 17:30:33', null, '0');

-- ----------------------------
-- Table structure for sys_area
-- ----------------------------
DROP TABLE IF EXISTS `sys_area`;
CREATE TABLE `sys_area` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `parent_id` varchar(64) NOT NULL COMMENT '父级编号',
  `parent_ids` varchar(2000) NOT NULL COMMENT '所有父级编号',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `sort` decimal(10,0) NOT NULL COMMENT '排序',
  `code` varchar(100) DEFAULT NULL COMMENT '区域编码',
  `type` char(1) DEFAULT NULL COMMENT '区域类型',
  `create_by` varchar(64) NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `sys_area_parent_id` (`parent_id`),
  KEY `sys_area_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区域表';

-- ----------------------------
-- Records of sys_area
-- ----------------------------
INSERT INTO `sys_area` VALUES ('1', '0', '0,', '中国', '10', '100000', '1', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_area` VALUES ('2', '1', '0,1,', '山东省', '20', '110000', '2', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_area` VALUES ('2c2948e6cb0144debe50903c44a097fa', '73afba17ebcf47cdb1caa859ad3e9b8d', '0,1,73afba17ebcf47cdb1caa859ad3e9b8d,', '成都', '30', '', '3', '1', '2016-11-03 16:28:31', '1', '2016-11-03 16:28:31', '', '0');
INSERT INTO `sys_area` VALUES ('3', '2', '0,1,2,', '济南市', '30', '110101', '3', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_area` VALUES ('4', '3', '0,1,2,3,', '历城区', '40', '110102', '4', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_area` VALUES ('5', '3', '0,1,2,3,', '历下区', '50', '110104', '4', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_area` VALUES ('6', '3', '0,1,2,3,', '高新区', '60', '110105', '4', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_area` VALUES ('73afba17ebcf47cdb1caa859ad3e9b8d', '1', '0,1,', '四川', '30', '', '2', '1', '2016-11-03 16:28:23', '1', '2016-11-03 16:28:23', '', '0');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `value` varchar(100) NOT NULL COMMENT '数据值',
  `label` varchar(100) NOT NULL COMMENT '标签名',
  `type` varchar(100) NOT NULL COMMENT '类型',
  `description` varchar(100) NOT NULL COMMENT '描述',
  `sort` decimal(10,0) NOT NULL COMMENT '排序（升序）',
  `parent_id` varchar(64) DEFAULT '0' COMMENT '父级编号',
  `create_by` varchar(64) NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `sys_dict_value` (`value`),
  KEY `sys_dict_label` (`label`),
  KEY `sys_dict_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='字典表';

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('1', '0', '正常', 'del_flag', '删除标记', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('10', 'yellow', '黄色', 'color', '颜色值', '40', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('100', 'java.util.Date', 'Date', 'gen_java_type', 'Java类型\0\0', '50', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('101', 'com.pcitech.iLife.modules.sys.entity.User', 'User', 'gen_java_type', 'Java类型\0\0', '60', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('102', 'com.pcitech.iLife.modules.sys.entity.Office', 'Office', 'gen_java_type', 'Java类型\0\0', '70', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('103', 'com.pcitech.iLife.modules.sys.entity.Area', 'Area', 'gen_java_type', 'Java类型\0\0', '80', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('104', 'Custom', 'Custom', 'gen_java_type', 'Java类型\0\0', '90', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('105', '1', '会议通告\0\0\0\0', 'oa_notify_type', '通知通告类型', '10', '0', '1', '2013-11-08 00:00:00', '1', '2013-11-08 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('106', '2', '奖惩通告\0\0\0\0', 'oa_notify_type', '通知通告类型', '20', '0', '1', '2013-11-08 00:00:00', '1', '2013-11-08 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('107', '3', '活动通告\0\0\0\0', 'oa_notify_type', '通知通告类型', '30', '0', '1', '2013-11-08 00:00:00', '1', '2013-11-08 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('108', '0', '草稿', 'oa_notify_status', '通知通告状态', '10', '0', '1', '2013-11-08 00:00:00', '1', '2013-11-08 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('109', '1', '发布', 'oa_notify_status', '通知通告状态', '20', '0', '1', '2013-11-08 00:00:00', '1', '2013-11-08 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('11', 'orange', '橙色', 'color', '颜色值', '50', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('110', '0', '未读', 'oa_notify_read', '通知通告状态', '10', '0', '1', '2013-11-08 00:00:00', '1', '2013-11-08 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('111', '1', '已读', 'oa_notify_read', '通知通告状态', '20', '0', '1', '2013-11-08 00:00:00', '1', '2013-11-08 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('12', 'default', '默认主题', 'theme', '主题方案', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('13', 'cerulean', '天蓝主题', 'theme', '主题方案', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('14', 'readable', '橙色主题', 'theme', '主题方案', '30', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('15', 'united', '红色主题', 'theme', '主题方案', '40', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('16', 'flat', 'Flat主题', 'theme', '主题方案', '60', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('17', '1', '国家', 'sys_area_type', '区域类型', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('18', '2', '省份、直辖市', 'sys_area_type', '区域类型', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('19', '3', '地市', 'sys_area_type', '区域类型', '30', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('2', '1', '删除', 'del_flag', '删除标记', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('20', '4', '区县', 'sys_area_type', '区域类型', '40', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('21', '1', '公司', 'sys_office_type', '机构类型', '60', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('22', '2', '部门', 'sys_office_type', '机构类型', '70', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('23', '3', '小组', 'sys_office_type', '机构类型', '80', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('24', '4', '其它', 'sys_office_type', '机构类型', '90', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('25', '1', '综合部', 'sys_office_common', '快捷通用部门', '30', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('26', '2', '开发部', 'sys_office_common', '快捷通用部门', '40', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('27', '3', '人力部', 'sys_office_common', '快捷通用部门', '50', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('28', '1', '一级', 'sys_office_grade', '机构等级', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('29', '2', '二级', 'sys_office_grade', '机构等级', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('3', '1', '显示', 'show_hide', '显示/隐藏', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('30', '3', '三级', 'sys_office_grade', '机构等级', '30', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('31', '4', '四级', 'sys_office_grade', '机构等级', '40', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('32', '1', '所有数据', 'sys_data_scope', '数据范围', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('33', '2', '所在公司及以下数据', 'sys_data_scope', '数据范围', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('34', '3', '所在公司数据', 'sys_data_scope', '数据范围', '30', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('35', '4', '所在部门及以下数据', 'sys_data_scope', '数据范围', '40', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('36', '5', '所在部门数据', 'sys_data_scope', '数据范围', '50', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('37', '8', '仅本人数据', 'sys_data_scope', '数据范围', '90', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('38', '9', '按明细设置', 'sys_data_scope', '数据范围', '100', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('39', '1', '系统管理', 'sys_user_type', '用户类型', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('4', '0', '隐藏', 'show_hide', '显示/隐藏', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('40', '2', '部门经理', 'sys_user_type', '用户类型', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('41', '3', '普通用户', 'sys_user_type', '用户类型', '30', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('42', 'basic', '基础主题', 'cms_theme', '站点主题', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('43', 'blue', '蓝色主题', 'cms_theme', '站点主题', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('44', 'red', '红色主题', 'cms_theme', '站点主题', '30', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('45', 'article', '文章模型', 'cms_module', '栏目模型', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('46', 'picture', '图片模型', 'cms_module', '栏目模型', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('47', 'download', '下载模型', 'cms_module', '栏目模型', '30', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('48', 'link', '链接模型', 'cms_module', '栏目模型', '40', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('49', 'special', '专题模型', 'cms_module', '栏目模型', '50', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('5', '1', '是', 'yes_no', '是/否', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('50', '0', '默认展现方式', 'cms_show_modes', '展现方式', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('51', '1', '首栏目内容列表', 'cms_show_modes', '展现方式', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('52', '2', '栏目第一条内容', 'cms_show_modes', '展现方式', '30', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('53', '0', '发布', 'cms_del_flag', '内容状态', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('54', '1', '删除', 'cms_del_flag', '内容状态', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('55', '2', '审核', 'cms_del_flag', '内容状态', '15', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('56', '1', '首页焦点图', 'cms_posid', '推荐位', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('57', '2', '栏目页文章推荐', 'cms_posid', '推荐位', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('58', '1', '咨询', 'cms_guestbook', '留言板分类', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('59', '2', '建议', 'cms_guestbook', '留言板分类', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('6', '0', '否', 'yes_no', '是/否', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('60', '3', '投诉', 'cms_guestbook', '留言板分类', '30', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('61', '4', '其它', 'cms_guestbook', '留言板分类', '40', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('62', '1', '公休', 'oa_leave_type', '请假类型', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('63', '2', '病假', 'oa_leave_type', '请假类型', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('64', '3', '事假', 'oa_leave_type', '请假类型', '30', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('65', '4', '调休', 'oa_leave_type', '请假类型', '40', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('66', '5', '婚假', 'oa_leave_type', '请假类型', '60', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('67', '1', '接入日志', 'sys_log_type', '日志类型', '30', '0', '1', '2013-06-03 00:00:00', '1', '2013-06-03 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('68', '2', '异常日志', 'sys_log_type', '日志类型', '40', '0', '1', '2013-06-03 00:00:00', '1', '2013-06-03 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('69', 'leave', '请假流程', 'act_type', '流程类型', '10', '0', '1', '2013-06-03 00:00:00', '1', '2013-06-03 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('7', 'red', '红色', 'color', '颜色值', '10', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('70', 'test_audit', '审批测试流程', 'act_type', '流程类型', '20', '0', '1', '2013-06-03 00:00:00', '1', '2013-06-03 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('71', '1', '分类1', 'act_category', '流程分类', '10', '0', '1', '2013-06-03 00:00:00', '1', '2013-06-03 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('72', '2', '分类2', 'act_category', '流程分类', '20', '0', '1', '2013-06-03 00:00:00', '1', '2013-06-03 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('73', 'crud', '增删改查', 'gen_category', '代码生成分类', '10', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('74', 'crud_many', '增删改查（包含从表）', 'gen_category', '代码生成分类', '20', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('75', 'tree', '树结构', 'gen_category', '代码生成分类', '30', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('76', '=', '=', 'gen_query_type', '查询方式', '10', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('77', '!=', '!=', 'gen_query_type', '查询方式', '20', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('78', '&gt;', '&gt;', 'gen_query_type', '查询方式', '30', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('79', '&lt;', '&lt;', 'gen_query_type', '查询方式', '40', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('8', 'green', '绿色', 'color', '颜色值', '20', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('80', 'between', 'Between', 'gen_query_type', '查询方式', '50', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('81', 'like', 'Like', 'gen_query_type', '查询方式', '60', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('82', 'left_like', 'Left Like', 'gen_query_type', '查询方式', '70', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('83', 'right_like', 'Right Like', 'gen_query_type', '查询方式', '80', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('84', 'input', '文本框', 'gen_show_type', '字段生成方案', '10', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('85', 'textarea', '文本域', 'gen_show_type', '字段生成方案', '20', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('86', 'select', '下拉框', 'gen_show_type', '字段生成方案', '30', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('87', 'checkbox', '复选框', 'gen_show_type', '字段生成方案', '40', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('88', 'radiobox', '单选框', 'gen_show_type', '字段生成方案', '50', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('89', 'dateselect', '日期选择', 'gen_show_type', '字段生成方案', '60', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('9', 'blue', '蓝色', 'color', '颜色值', '30', '0', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('90', 'userselect', '人员选择\0', 'gen_show_type', '字段生成方案', '70', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('91', 'officeselect', '部门选择', 'gen_show_type', '字段生成方案', '80', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('92', 'areaselect', '区域选择', 'gen_show_type', '字段生成方案', '90', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('93', 'String', 'String', 'gen_java_type', 'Java类型', '10', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('94', 'Long', 'Long', 'gen_java_type', 'Java类型', '20', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('95', 'dao', '仅持久层', 'gen_category', '代码生成分类\0\0\0\0\0\0', '40', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('96', '1', '男', 'sex', '性别', '10', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('97', '2', '女', 'sex', '性别', '20', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '0');
INSERT INTO `sys_dict` VALUES ('98', 'Integer', 'Integer', 'gen_java_type', 'Java类型\0\0', '30', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');
INSERT INTO `sys_dict` VALUES ('99', 'Double', 'Double', 'gen_java_type', 'Java类型\0\0', '40', '0', '1', '2013-10-28 00:00:00', '1', '2013-10-28 00:00:00', '', '1');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `type` char(1) DEFAULT '1' COMMENT '日志类型',
  `title` varchar(255) DEFAULT '' COMMENT '日志标题',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `remote_addr` varchar(255) DEFAULT NULL COMMENT '操作IP地址',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '用户代理',
  `request_uri` varchar(255) DEFAULT NULL COMMENT '请求URI',
  `method` varchar(5) DEFAULT NULL COMMENT '操作方式',
  `params` text COMMENT '操作提交的数据',
  `exception` text COMMENT '异常信息',
  PRIMARY KEY (`id`),
  KEY `sys_log_create_by` (`create_by`),
  KEY `sys_log_request_uri` (`request_uri`),
  KEY `sys_log_type` (`type`),
  KEY `sys_log_create_date` (`create_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='日志表';

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES ('00a7fe60d07942888569d3a4ca7c2307', '1', '内容管理-内容管理', '1', '2016-11-03 14:09:10', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('00e1a6cc07f6408098a5d1450c9d38af', '1', '我的面板-个人信息-个人信息', '295def53ef614f06b164f78c4944eb4d', '2016-11-04 12:45:53', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('014ef206521d49ef9cee16605817ab83', '1', '内容管理-栏目设置-站点设置', '1', '2016-11-03 14:12:15', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/site/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('02388dc07ac5415e883c4e3979bdb09a', '1', '内容管理-栏目设置-栏目管理-查看', '1', '2016-11-03 14:49:10', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('02529e6d66894a9dbd59f8c6185437e0', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:32:01', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=0,', '');
INSERT INTO `sys_log` VALUES ('02f596677fbc446e9ed07cddb959dfd8', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:43:19', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('037c970f965a4c84bbe44bfbdf099e97', '1', '我的面板-个人信息-修改密码', '1', '2016-11-03 16:03:51', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/modifyPwd', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('038634cbc34e4b6da07ad447b07cc285', '1', '系统登录', '1', '2016-11-03 17:22:19', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a', 'GET', 'login=', '');
INSERT INTO `sys_log` VALUES ('03cbe469783046c78b2f5cf7e836d29c', '1', '内容管理-内容管理', '1', '2016-11-03 16:39:43', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('03d08648945d482ca8d8977ad49a689e', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:33:20', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('03d2f04404d84a028776794695a8a119', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:25:28', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('046da8a646df4cfa80c4e0014cbf5aaa', '1', '系统设置-机构用户-机构管理-修改', '1', '2016-11-03 16:29:15', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/delete', 'GET', 'id=7a07e97f539f49e7b6122a20b758d028', '');
INSERT INTO `sys_log` VALUES ('04c04d8a24ea4fc79575d4498b5a7a66', '1', '系统设置-系统设置-角色管理', '1', '2016-11-03 16:38:59', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('0522a61eea8442fe9f207892ad56e066', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:27:58', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/form', 'GET', 'parent.id=', '');
INSERT INTO `sys_log` VALUES ('06edab267a5b445587671c7d947107f7', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:05:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('08955f043fd544a9931f18f741a35a15', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 16:26:17', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('08a4181e606e406c9e5357b2989daf19', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:50:02', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('090c3442cddb4cdbb5a1301324ba45f0', '1', '系统设置-系统设置-角色管理-查看', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:57', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/form', 'GET', 'id=2', '');
INSERT INTO `sys_log` VALUES ('0919b3c2aa2345b09c25b54b9897e7c3', '1', '内容管理-内容管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:24', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('09f6da31d50b43b68a66f78c43e525d6', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:18:54', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('0a1adb5315be4feba86d1fad10cf30f9', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 16:29:31', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('0a577c6f1bed4a9ebcc8f522121b02b6', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:32:48', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', 'repage=', '');
INSERT INTO `sys_log` VALUES ('0adc8a2a0db54c2a96e8ab757b6c1e01', '1', '代码生成-代码生成-业务表配置', '1', '2016-11-03 13:47:42', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/gen/genTable', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('0add53b33efc4d7287ce07cb15b7d279', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:25:07', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('0b2e68a0546e42569e3bb8397742e8fe', '1', '在线办公-通知通告-我的通告', '1', '2016-11-03 13:47:40', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('0b32dc0b80104bda9dd0628e366be57c', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 16:46:41', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('0bdaf82be5b544028b5b82b74621597b', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:12:35', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('0c8b29a810d54fde91121257ae2e4832', '1', '系统设置-系统设置-字典管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:45', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/dict/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('0d249e978b6249dfa9ce8fe3990b1a97', '1', '内容管理-内容管理', '1', '2016-11-03 14:08:54', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('0dd80876c9a14d03be6a2bfae5818210', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:07:37', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('0de17bc03d834becb0a86aedc9d5cc7f', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 17:07:25', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('102314ebe4614f7e85823339f8636c19', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 13:47:34', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', 'tabPageId=jerichotabiframe_0', '');
INSERT INTO `sys_log` VALUES ('102ec977085444aea9655f2712213700', '1', '内容管理-内容管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-04 12:49:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('1078d08e126f4476805b785ef7eb8a6f', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 14:50:11', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('109e5fa6662e419eb6eab462fbcc57b3', '1', '系统登录', '1', '2016-11-03 14:04:57', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('1116026bdce34e1c84f8a8da7e08ad8e', '1', '系统登录', '1', '2016-11-03 15:39:08', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a', 'GET', 'login=', '');
INSERT INTO `sys_log` VALUES ('131b8b916f9d44b083bb9eea1a2484e9', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 16:52:47', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('1351f3944f5441ec9ad3bdcfa6b025b4', '1', '系统设置-系统设置-角色管理', '1', '2016-11-03 16:38:58', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('1406108171584b4f898ca2915aa8191b', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 14:20:14', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('15c95b78cc6041569cf22b50aafa9985', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:29:52', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=', '');
INSERT INTO `sys_log` VALUES ('16a293c6ad144380a19bcbc846bd3ffe', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 14:47:26', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('17841f03ad764dac934a90bca8d5ba00', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:12:15', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('1883900abd4e445e8449cc2a5806b198', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:31:29', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/form', 'GET', 'parent.id=54d05cbfef4f4bafb39ec67d0d26211c', '');
INSERT INTO `sys_log` VALUES ('18e3510c866f4146bbba8e53aeaa6032', '1', '内容管理-内容管理', '1', '2016-11-03 16:44:18', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('19cbb1f84c014e818c9af255eafae62f', '1', '系统设置-系统设置-字典管理', '1', '2016-11-03 14:05:23', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/dict/', 'POST', 'pageNo=3&pageSize=30&type=act_category&description=', '');
INSERT INTO `sys_log` VALUES ('19ed4667759348658dc0e73b02341bed', '1', '系统设置-系统设置-菜单管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:45', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/menu/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('19ff984e8c434bdc8f0ff057638bfb8f', '1', '内容管理-内容管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:24', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('1a234afcee834716b229c74d358d7a7a', '1', '系统登录', '295def53ef614f06b164f78c4944eb4d', '2016-11-04 12:50:03', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('1ba5bcfa2bd94a689e269a0b5cce354f', '1', '系统设置-机构用户-机构管理', '1', '2016-11-03 16:29:51', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('1c967a87b4ea4ee8bc3243f7a919c9ab', '1', '在线办公-个人办公-审批测试', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 17:22:11', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('1cbbe23ae1704ad193bbbfc311e18480', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:33:13', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('1cf9feca79c2493e980f93c9d6d480f8', '1', '系统登录', '295def53ef614f06b164f78c4944eb4d', '2016-11-04 12:45:49', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a', 'GET', 'login=', '');
INSERT INTO `sys_log` VALUES ('1d18cb3fd5c04615bba03f7c56f10b19', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 15:39:09', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('1e14cebb6f0e4ecb83e5344026a2f40a', '1', '系统设置-系统设置-角色管理-查看', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:49', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/form', 'GET', 'id=2', '');
INSERT INTO `sys_log` VALUES ('1e67ae06684a4ccfaa697173a85758d5', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:29:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=', '');
INSERT INTO `sys_log` VALUES ('1eb1026bda6a468e94e0b455facea721', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:15:58', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('1fee795809cb4ba68e0be18da8767a10', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:29:19', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/form', 'GET', 'parent.id=54d05cbfef4f4bafb39ec67d0d26211c', '');
INSERT INTO `sys_log` VALUES ('20036561fff243cd9443288bc916c646', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:32:06', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=', '');
INSERT INTO `sys_log` VALUES ('2086fc488fde4764a91191feb9b0ed0a', '1', '系统登录', '1', '2016-11-03 16:35:26', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/save', 'POST', 'id=&photo=/iLife/userfiles/1/images/photo/2016/11/4c69eb5b0d4761b6da06a22e3d950f06.jpg&company.id=54d05cbfef4f4bafb39ec67d0d26211c&company.name=PCI&office.id=56c6fb3511a548c09fb0964b00b2145c&office.name=测试组&no=001&name=审核人1&oldLoginName=&loginName=shenheren1&newPassword=&confirmNewPassword=&email=&phone=&mobile=&loginFlag=1&userType=1&roleIdList=2&_roleIdList=on&remarks=', '');
INSERT INTO `sys_log` VALUES ('21b3131d1f01442bbb920e4f6cb8bb1b', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 17:07:33', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('223a4b8311f84b549d9ee39a0b2622ef', '1', '内容管理-栏目设置-站点设置', '1', '2016-11-03 14:07:43', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/site/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('227dbfd09ae8433fbaf439e9ff9d35e0', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 13:48:26', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('22d65cf8cc8f44e094b174edb50424c3', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 16:49:44', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('24441edc24cb4bfdb338cc48c4090854', '1', '内容管理-内容管理-评论管理-查看', '1', '2016-11-03 14:12:32', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/comment/', 'GET', 'status=2', '');
INSERT INTO `sys_log` VALUES ('2468a8b981ca4c5c8bbb899f26bd529d', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 16:47:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('25a15b199ce5485cba6da95cb82c237f', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 14:19:13', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('2604ca7354bc4024b2dd5ec6c39f521e', '1', '在线办公-通知通告-我的通告', '1', '2016-11-03 14:50:31', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('265c1bfc7df6410183d77bf57d1d7f2d', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 16:43:18', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('280363251e014f22935d1b0442aa4736', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 16:47:58', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('29c24fae31064de1ba5e78a07cbef0b4', '1', '系统设置-系统设置-角色管理', '1', '2016-11-03 16:37:01', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('2a0e2208fe254db69651e6022d332f17', '1', '内容管理-内容管理', '1', '2016-11-03 14:12:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('2aac36890e734ef3814d5ee25b83553a', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:29:48', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=', '');
INSERT INTO `sys_log` VALUES ('2b1bacb8bfc2439f94022219bebb039b', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:27:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=', '');
INSERT INTO `sys_log` VALUES ('2bbaecec99b947e4b7994a04647114ea', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 14:13:35', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('2c21cf72bfbf49ea8bce635bff233edf', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 14:19:19', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('2c26b2e0ad964241b446a52263f4e7f4', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 13:46:07', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('2daf5fdbddaa4815a94d636d906e521d', '1', '系统设置-系统设置-菜单管理', '1', '2016-11-03 16:36:59', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/menu/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('2e53186a2ac24360bef83ce9b96b3ce6', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:12:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('2e8ea9f5019d4d29b78c62abffc45981', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:13:37', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('2fdce07e52384e5c97c86babbf6bac1f', '1', '内容管理-内容管理', '1', '2016-11-03 14:12:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('302f36ec05324393801f115b57c85e81', '1', '内容管理-栏目设置-站点设置', '1', '2016-11-03 14:12:25', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/site/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('31f9d5f114bb4b28ab0331201e52dd3f', '1', '在线办公-个人办公-我的任务', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 17:22:07', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('3255eee2f23145ad9fe62d6c6525952f', '1', '在线办公-流程管理-流程管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 17:21:58', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('33326ed7f198405fa56aeaec3656cbb6', '1', '内容管理-内容管理', '1', '2016-11-03 16:04:32', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('339d84d001f34c7cbae9f424d6fdc96c', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:07:33', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('34641f7b78ee41f78495333c7676acf8', '1', '内容管理-栏目设置-栏目管理-修改', '1', '2016-11-03 14:50:02', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/save', 'POST', 'id=&office.id=1&office.name=山东省总公司&parent.id=1&parent.name=顶级栏目&module=&name=ss&image=/iLife/userfiles/1/_thumbs/images/cms/category/2016/11/Chrysanthemum.jpg&href=&target=&description=&keywords=&sort=30&inMenu=0&inList=1&showModes=0&allowComment=0&isAudit=0&customListView=&customContentView=&viewConfig=', '');
INSERT INTO `sys_log` VALUES ('34eb6809040245b0872ed626c9c42f4d', '1', '内容管理-栏目设置-站点设置', '1', '2016-11-03 16:39:40', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/site/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('36827be376b74b60bb0c83e9daf02559', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 16:47:42', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('36a63cb4901c459b8d8a5bab029cb115', '1', '内容管理-栏目设置-栏目管理-查看', '1', '2016-11-03 14:22:01', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('37932d7916c540eb9f91b7a43b2ab5ad', '1', '系统设置-系统设置-菜单管理', '1', '2016-11-03 16:43:22', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/menu/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('37cefd8b2b9444a8a3483fb5408d5db9', '1', '内容管理-栏目设置-栏目管理-查看', '1', '2016-11-03 14:18:03', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('388e4c82abf542e5828b5a6ec0a35229', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 16:25:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('3895f8be9bd14b46a35e1de3170d37c0', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:22:26', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('38edc04f8e5a4b9bba31d1121423a63f', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:03:21', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('394d88a54b084f29b810f54877aa726e', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:27:39', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('39e7af7d32b840f2b69afa5c8beb53e5', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 14:13:32', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('3a06062cfae94383aed84b86f7263b8c', '1', '系统设置-机构用户-机构管理', '1', '2016-11-03 16:27:26', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('3bcf60963eac4cf7bdb0232c9ee12af9', '1', '内容管理-栏目设置-栏目管理-查看', '1', '2016-11-03 14:16:08', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('3c151677b3184c0681a2b748addbf92e', '1', '系统设置-机构用户-区域管理-修改', '1', '2016-11-03 16:28:23', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/area/save', 'POST', 'id=&parent.id=1&parent.name=中国&name=四川&code=&type=2&remarks=', '');
INSERT INTO `sys_log` VALUES ('3ce5fadde41944d08c1e906320eaf946', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 14:44:07', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('3de0f887c7c9436598c1844c16af413a', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 14:07:36', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('3e66827d34a946e09cb59116e85cc5de', '1', '内容管理-栏目设置-栏目管理-查看', '1', '2016-11-03 14:12:18', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('404fa99cd1cb4637832aa1fddabbd407', '1', '系统登录', '1', '2016-11-03 16:02:57', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a', 'GET', 'login=', '');
INSERT INTO `sys_log` VALUES ('4056e0b745724427bd1a8d12c3be73cf', '1', '在线办公-通知通告-通告管理', '1', '2016-11-03 16:04:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('406602b246b14ce280f9fe6844d35479', '1', '内容管理-栏目设置-栏目管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:26', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('4114fe3728fa4134af92733f39c5cf2b', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 17:22:43', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('4120de9c6bb44fe1a393e2a233c0138c', '1', '系统设置-系统设置-角色管理', '1', '2016-11-03 16:38:57', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('41c197fc6380496993e23767a78ec56f', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:27:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=', '');
INSERT INTO `sys_log` VALUES ('4278be05c0fd44ad98c83922113aa9e7', '1', '内容管理-内容管理-内容发布-文章模型', '1', '2016-11-03 14:50:21', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/article/', 'GET', 'category.id=3', '');
INSERT INTO `sys_log` VALUES ('42942f6f288c42949651f32e05fb30fc', '1', '系统设置-机构用户-机构管理-修改', '1', '2016-11-03 16:31:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/save', 'POST', 'id=&parent.id=54d05cbfef4f4bafb39ec67d0d26211c&parent.name=PCI&area.id=2c2948e6cb0144debe50903c44a097fa&area.name=成都&name=测试组&code=001&type=2&grade=2&useable=1&primaryPerson.id=&office.primaryPerson.name=&deputyPerson.id=&office.deputyPerson.name=&address=&zipCode=&master=&phone=&fax=&email=&remarks=&_childDeptList=on', '');
INSERT INTO `sys_log` VALUES ('435d87bae72a4471b4259893e2583671', '1', '系统设置-机构用户-用户管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:32', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('43839940baab48ef8e23d94898eaef9f', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 17:22:25', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('43881254101043ea9789f589133dbbbb', '1', '系统设置-机构用户-区域管理-查看', '1', '2016-11-03 16:28:25', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/area/form', 'GET', 'parent.id=73afba17ebcf47cdb1caa859ad3e9b8d', '');
INSERT INTO `sys_log` VALUES ('439a5afeec97405290feb2f6765cb3d2', '1', '内容管理-内容管理', '1', '2016-11-03 14:49:04', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('45a11d8cd3ca43f2905f6c59e9714503', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:03:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('46999f361f344d1b9efda425891cd18f', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:28:32', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('46b56914389e4763b0b146bd03790cb0', '1', '系统设置-系统设置-角色管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:44', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('46ca113a16604661b2d00b09a8520c3c', '1', '内容管理-内容管理-评论管理-查看', '1', '2016-11-03 14:07:39', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/comment/', 'GET', 'status=2', '');
INSERT INTO `sys_log` VALUES ('46f2f2691f094656a5ba4ef498382cdd', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 13:46:13', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('48d4fb7b52ef4552bcc3be0cedc2e4d6', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:32:37', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/form', 'GET', 'id=1', '');
INSERT INTO `sys_log` VALUES ('493b7b32307544eca0f19836d735f7c3', '1', '内容管理-内容管理', '1', '2016-11-03 14:07:36', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('4a0fe2ca7a2947ec9d1af7ea777c2859', '1', '内容管理-内容管理-公共留言-查看', '1', '2016-11-03 14:50:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/guestbook/', 'GET', 'status=2', '');
INSERT INTO `sys_log` VALUES ('4b9ac79aba834f8b8ca9dcdb091adbf7', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 16:46:31', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('4c4aa93c600747b0879c6ed64c79acd5', '1', '系统设置-系统设置-角色管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:47', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('4c640d4ef39e47a398fd09202a7dd373', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:25:34', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', 'office.id=1&office.name=山东省总公司', '');
INSERT INTO `sys_log` VALUES ('4cf67cfa0ded44f4b4e94815b76fefee', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:36:04', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', 'repage=', '');
INSERT INTO `sys_log` VALUES ('4d2e5624b9bc43f68b6f9bff4d9fc95b', '1', '系统设置-系统设置-角色管理', '1', '2016-11-03 16:36:16', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('4eb70c954ae0419db003f5476d0ca167', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 13:52:41', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('4f2f208836c146ce9d0c3d6fa66c1402', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:03:48', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('50f07b7f74804cf7af7ef6e9c2225f5b', '1', '内容管理-栏目设置-栏目管理-查看', '1', '2016-11-03 14:15:54', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('51496f6634834d24b061a8d235c5a993', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 16:46:36', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('52d10afbb52e4944bb038d8b7ccc045c', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 14:08:53', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('52ff91983430404ca4a360af1035f8e0', '1', '内容管理-内容管理', '1', '2016-11-03 15:39:16', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('53a185dd79c3431e8d90ebd940d942b3', '1', '内容管理-内容管理', '1', '2016-11-03 13:46:13', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('53bb06631c70495b9436e89989a48f5a', '1', '内容管理-内容管理', '1', '2016-11-03 14:19:16', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('541074245b714de2a138acee4af650ac', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:09:54', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('552ea3c9d50f447692125e25f7815889', '1', '系统设置-机构用户-区域管理', '1', '2016-11-03 16:26:18', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/area/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('559e478fcde6445793eccf0f991b1039', '1', '内容管理-内容管理', '1', '2016-11-03 13:47:40', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('56b980ec9c2e4ba4b5a7c37fa9f4bb2f', '1', '系统登录', '1', '2016-11-03 16:42:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a', 'GET', 'login=', '');
INSERT INTO `sys_log` VALUES ('56e9e435db714237a8fea8819b9a2367', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 16:39:52', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('570d3cb181c34011924c7c23360a4484', '1', '系统设置-机构用户-区域管理', '1', '2016-11-03 16:28:23', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/area/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('578117b51fd04379a9bbd7fe27bfa353', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 15:50:23', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('5795db68364344b6a77d10c13aa70113', '1', '我的面板-个人信息-个人信息', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 17:21:47', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('580c067662184d37a152a935db435a53', '1', '系统设置-机构用户-用户管理-查看', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:32', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('584fb095bd7a42c99f430d1608b870a9', '1', '系统设置-机构用户-用户管理-修改', '1', '2016-11-03 16:32:48', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/save', 'POST', 'id=1&photo=/iLife/userfiles/1/images/photo/2016/11/4c69eb5b0d4761b6da06a22e3d950f06.jpg&company.id=54d05cbfef4f4bafb39ec67d0d26211c&company.name=PCI&office.id=56c6fb3511a548c09fb0964b00b2145c&office.name=测试组&no=0001&name=系统管理员&oldLoginName=admin&loginName=admin&newPassword=&confirmNewPassword=&email=&phone=8675&mobile=8675&loginFlag=1&userType=&roleIdList=2&_roleIdList=on&remarks=最高管理员', '');
INSERT INTO `sys_log` VALUES ('58509a4319384903899764eac18a3a96', '1', '内容管理-栏目设置-栏目管理-查看', '1', '2016-11-03 15:39:24', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/form', 'GET', 'id=1c67130c89b94d058acb4d1ff98e5322', '');
INSERT INTO `sys_log` VALUES ('58614a5260934c1fb49f48deddbeaf7f', '1', '系统登录', '1', '2016-11-03 17:05:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/WEB-INF/views/modules/act/actTaskTodoList.jsp', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('58e06deeafda4ae2999da7c12afcba79', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:07:31', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('5b485eeffbcb40b39635504cdfb8ff4e', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:35:26', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', 'repage=', '');
INSERT INTO `sys_log` VALUES ('5b58a80e087b45efb0badeacc71a4952', '1', '系统设置-机构用户-机构管理-修改', '1', '2016-11-03 16:31:25', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/save', 'POST', 'id=54d05cbfef4f4bafb39ec67d0d26211c&parent.id=&parent.name=&area.id=2c2948e6cb0144debe50903c44a097fa&area.name=成都&name=PCI&code=&type=1&grade=1&useable=1&primaryPerson.id=&office.primaryPerson.name=&deputyPerson.id=&office.deputyPerson.name=&address=&zipCode=&master=&phone=&fax=&email=&remarks=', '');
INSERT INTO `sys_log` VALUES ('5be8b99f529049ea9d41bb6fcbefee2f', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 16:04:32', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('5cbdf85fad064f53a4e879dea268302c', '1', '系统设置-机构用户-机构管理', '1', '2016-11-03 16:29:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('601810c6b20d44789671cd93650c1a61', '1', '系统设置-机构用户-区域管理-查看', '1', '2016-11-03 16:28:13', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/area/form', 'GET', 'parent.id=1', '');
INSERT INTO `sys_log` VALUES ('6038ffea0f514b2badf4e7f630e8d1e9', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 14:44:11', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('60aa0c93588a47bd9748cb5ec9c1f001', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 16:44:18', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('616033eb6c12464e89cbd77308ef2a76', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 14:09:10', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('62eccf8a7bb640f08ff4f6622c27bbd9', '1', '系统设置-机构用户-用户管理-修改', '1', '2016-11-03 16:34:55', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/checkLoginName', 'GET', 'oldLoginName=&loginName=shenheren1', '');
INSERT INTO `sys_log` VALUES ('635d12bf43d942009a40021e66a21e07', '1', '在线办公-通知通告-通告管理', '1', '2016-11-03 16:04:29', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('6487b67f7c344eb3a8a78912bc18bb2f', '1', '我的面板-个人信息-修改密码', '1', '2016-11-03 13:47:23', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/modifyPwd', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('648dcc5023c4481d858ce9cbae3d5548', '1', '系统设置-系统设置-角色管理-修改', '1', '2016-11-03 16:37:03', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/assign', 'GET', 'id=2', '');
INSERT INTO `sys_log` VALUES ('64b032cd5d934e248cccccaeb2fa5086', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 16:33:13', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('64e1787b6e3e4f4c8418ecd6b25a071d', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:35:39', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('65e8c7ee5ce04e5484184564ec9d29ff', '1', '系统设置-系统设置-字典管理', '1', '2016-11-03 14:05:09', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/dict/', 'POST', 'pageNo=3&pageSize=30&type=&description=', '');
INSERT INTO `sys_log` VALUES ('663845a6cb2844dfb08af31f1d79b7fb', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 14:12:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('66b639905fb542dd96a835b34abf4737', '1', '系统设置-系统设置-角色管理-修改', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:42:40', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/save', 'POST', 'id=2&office.id=56c6fb3511a548c09fb0964b00b2145c&office.name=测试组&oldName=公司管理员&name=公司管理员&oldEnname=hr&enname=hr&roleType=assignment&sysData=1&useable=1&dataScope=2&menuIds=1,27,28,29,30,71,56,57,58,59,62,88,89,90,63,73,74,64,65,66,69,70,72,31,40,41,42,43,44,45,46,47,48...&officeIds=&remarks=', '');
INSERT INTO `sys_log` VALUES ('67050465092041d9a3074ec66364c96a', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:39:46', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('674934e82763425b87762112f53b07fe', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 14:19:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('678142fe80a34324a5a54a60dd35cc1a', '1', '内容管理-内容管理-内容发布-文章模型-查看', '1', '2016-11-03 14:50:23', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/article/findByIds', 'GET', 'ids=', '');
INSERT INTO `sys_log` VALUES ('679d15f799fa4f24a1188100293fbe4a', '1', '内容管理-内容管理-评论管理-查看', '1', '2016-11-03 14:50:29', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/comment/', 'GET', 'status=2', '');
INSERT INTO `sys_log` VALUES ('68d46f4cc23949d8abb4409a74d33c37', '1', '内容管理-内容管理', '1', '2016-11-03 16:39:43', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('694d2be4934b408081fa86fe7fd2fdad', '1', '在线办公-通知通告-我的通告', '1', '2016-11-03 13:46:07', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('6993463c5c764d3790fb52373f57905a', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 16:32:08', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('69e079845e634d5eb51a35fa0bdffac4', '1', '内容管理-内容管理-公共留言-查看', '1', '2016-11-03 14:12:33', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/guestbook/', 'GET', 'status=2', '');
INSERT INTO `sys_log` VALUES ('6a68999f39874aee8b1e171ff8334863', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 16:47:44', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('6bc4a0ea34f94c31ac90e1dfe1c4f08c', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 13:46:08', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('6bff53f72b7b4025b19a0a0d49400c4e', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:36:19', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('6df45b5a0171449e8e98d861fea251f0', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 16:57:45', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('6f3a17c1ecc54da0b4b4e25396e211c0', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 16:47:29', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('71487aa26a3e429aa5957bc9cb32556f', '1', '内容管理-内容管理', '1', '2016-11-03 14:09:10', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('724c9809487e42ba86cd58278ee0016e', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:25:19', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'POST', 'photo=/iLife/userfiles/1/images/photo/2016/11/4c69eb5b0d4761b6da06a22e3d950f06.jpg&name=系统管理员&email=&phone=8675&mobile=8675&remarks=最高管理员', '');
INSERT INTO `sys_log` VALUES ('7285cfe894e14dc68d2bf980bcebb911', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:28:34', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('73fcadc0f25d47e8a76f04d1d73c1981', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 13:47:41', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('7470f39368e6403f918f6a3520873775', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:07:42', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('7561b0018c11497b95d66b25ee41cd7b', '1', '内容管理-栏目设置-站点设置', '1', '2016-11-03 14:48:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/site/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('757ab4e1f7ea4dfda95ce35026d99480', '1', '系统设置-机构用户-区域管理', '1', '2016-11-03 16:28:31', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/area/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('759c0d3c69a642fabe63d9ba24518919', '1', '系统设置-系统设置-菜单管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:43', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/menu/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('76068adaeca246b088782795ea13f2af', '1', '系统设置-机构用户-区域管理', '1', '2016-11-03 16:29:49', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/area/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('76fc8aacce374cc3aeb4fe7a71e771ba', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 14:19:12', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('7a63bd9ed0f64b33ae66975c3c3e9fbf', '1', '在线办公-通知通告-我的通告', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:20', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('7c631ca7cd784f25b8624fd1aa57cffc', '1', '在线办公-通知通告-我的通告', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 17:21:52', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('7ccf87140ed542e68b4982b655fa176a', '1', '内容管理-内容管理-内容发布', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:24', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('7e0c3c523dc64af7b44df10e2774085a', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:32:08', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('7e51646189f54c1e8db7afa26c3ea115', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:31:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=54d05cbfef4f4bafb39ec67d0d26211c&parentIds=0,54d05cbfef4f4bafb39ec67d0d26211c,', '');
INSERT INTO `sys_log` VALUES ('7e58fadae8cc425b852484bf9577ea39', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:29:25', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=54d05cbfef4f4bafb39ec67d0d26211c&parentIds=', '');
INSERT INTO `sys_log` VALUES ('7ef15ce1033c4674a3d1187e3d1616e8', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:31:05', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=0,', '');
INSERT INTO `sys_log` VALUES ('7f9b6982f78a4142bd27fae5f87425a5', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 16:52:58', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('7f9f65450ace4dbaaccdb0f4fda0c304', '1', '内容管理-内容管理', '1', '2016-11-03 16:39:48', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('80de4a51c71342f08912085ca8181e9c', '1', '在线办公-通知通告-我的通告', '1', '2016-11-03 14:19:17', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('80f644c2030041b3a8ce0147ae9eb033', '1', '内容管理-内容管理', '1', '2016-11-03 13:47:40', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('81d3136cca824b66b886d86a7a5a3642', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 16:27:38', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8247e91dfbd14c45b719e9b4ebef450f', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 14:50:33', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('824e7201a9d249ce935f35241035cc24', '1', '系统设置-机构用户-用户管理-修改', '1', '2016-11-03 16:35:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/checkLoginName', 'GET', 'oldLoginName=&loginName=shenheren2', '');
INSERT INTO `sys_log` VALUES ('83b00798947a4688a91ec07013ded53e', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:03:31', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('83c4de9924054c4e8df49464ced692cc', '1', '系统设置-系统设置-字典管理', '1', '2016-11-03 14:08:01', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/dict/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('842fffc6828c4a9680792c61c97ee5a9', '1', '在线办公-流程管理-模型管理', '1', '2016-11-03 14:20:11', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/model', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8434a482caa74153bc770cb0dc39232a', '1', '内容管理-内容管理', '1', '2016-11-03 16:04:32', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('84ebf14a15914b35bffd5e26fdad37f3', '1', '系统设置-系统设置-角色管理', '1', '2016-11-03 16:37:07', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8568576f59bb441a91b5565e73cca9d5', '1', '系统设置-系统设置-字典管理', '1', '2016-11-03 16:43:25', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/dict/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8574a6dcce204fda9be21917fac186b5', '1', '内容管理-内容管理', '1', '2016-11-03 14:48:22', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('85d2ccb641934b47856d0d4f2d82d318', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 16:25:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('85d9e7ae347241ad8eea1545ba72f8f2', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 13:47:36', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('87b58d9746544c2e8a84ea524e0778e3', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 16:47:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('87e11185e1ed47ac8e57bd94b6332899', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 13:53:21', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('88d743f9419c414283404a028492fe70', '1', '内容管理-内容管理', '1', '2016-11-03 15:39:16', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8950566b60a3478da686c26b7e7e222a', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 16:47:59', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('89671d3175d24db18a1f7695fd71472c', '1', '系统设置-日志查询-日志查询', '1', '2016-11-03 16:43:39', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/log', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8992886e1c4d48b2a7556d2117b7e9ef', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:26:31', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('89c0c5b8817b4aed9413dbde2e74fb52', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 16:46:20', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('89c93930e9dd4500948ca91d779b62b2', '1', '在线办公-通知通告-我的通告', '1', '2016-11-03 17:22:22', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('89d6ad515be24fbbb08d4cb3065ffa8b', '1', '系统设置-机构用户-区域管理-修改', '1', '2016-11-03 16:28:31', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/area/save', 'POST', 'id=&parent.id=73afba17ebcf47cdb1caa859ad3e9b8d&parent.name=四川&name=成都&code=&type=3&remarks=', '');
INSERT INTO `sys_log` VALUES ('89f6cdd7e5bc4495a5fa7340bce9ec9f', '1', '在线办公-流程管理-模型管理', '1', '2016-11-03 14:06:38', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/model', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8a53bf20e41141a5bb1e151469ed644e', '1', '在线办公-个人办公-我的任务', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 17:22:11', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8aaa502a2d9a47d2827b75e0e4c3078c', '1', '代码生成-代码生成-业务表配置', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/gen/genTable', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8ae58c7943cf4d81b63fcd51fee6371e', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:29:34', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/form', 'GET', 'id=1', '');
INSERT INTO `sys_log` VALUES ('8b61617486ef42e7b8961f7403664e76', '1', '在线办公-流程管理-模型管理', '1', '2016-11-03 16:46:35', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/model', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8c5fb888b61246c296929d60e25d1025', '1', '在线办公-流程管理-模型管理', '1', '2016-11-03 13:49:42', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/model', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8c791df5999646239c1435b05aa10742', '1', '在线办公-通知通告-我的通告', '1', '2016-11-03 16:46:18', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8cd226f476884760a0a97ee8872dd47a', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:25:40', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', 'repage=', '');
INSERT INTO `sys_log` VALUES ('8db630c2e2064296b2af33f4b2d4190f', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 17:22:20', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8e0b388b957845389224098932773c60', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 13:54:20', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8ea7bffe59a54a0e94f8650544eb4363', '1', '在线办公-通知通告-我的通告', '1', '2016-11-03 14:06:11', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8f889c8524ed4fd6bf3d8dbfaff9807a', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:08:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8facb5fdbd7145d488c8c8d60771700d', '1', '我的面板-个人信息-个人信息', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:18', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('8fae5ee26f514b5da5c8126512dcc955', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:31:25', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=0,', '');
INSERT INTO `sys_log` VALUES ('8fd42c60f7884c00b14b4b09d842a80d', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 13:47:45', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('924d01bb646b4be393a6630aaf71242d', '1', '内容管理-内容管理-内容发布', '295def53ef614f06b164f78c4944eb4d', '2016-11-04 12:49:26', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('928ed147ed544ed0b204ed3aed85b23f', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:49:07', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('93ae2ac1344c4673af60efa8ab2ec406', '1', '系统设置-机构用户-区域管理', '1', '2016-11-03 16:28:10', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/area/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('9429710794d845139c1e92d93a9f78a5', '1', '内容管理-内容管理', '1', '2016-11-03 14:19:16', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('948481833e20413a9d5b26d9f8389218', '1', '内容管理-内容管理-内容发布-文章模型-查看', '1', '2016-11-03 14:50:23', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/article/form', 'GET', 'category.name=网站简介&id=&category.id=3', '');
INSERT INTO `sys_log` VALUES ('9514ef704b3a449bbf0ac1115033d0e6', '1', '系统设置-日志查询-日志查询', '1', '2016-11-03 16:43:31', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/log', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('953628290a784346af52410382f3b0c7', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:26:22', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('9640e2f611cd43718ccdc723d2e49e3b', '1', '内容管理-内容管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 17:21:50', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('96b3294a945c45199f73c313a9effe1e', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 16:39:43', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('96f258c5bc0a45dc9a6c9f95a82662dd', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:09:12', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('96f5f16c576e4a19981d27ab988e0f71', '1', '内容管理-内容管理', '1', '2016-11-03 14:13:35', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('9724063a0a654ae0a5fc01ad548ea911', '1', '内容管理-内容管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-04 12:49:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('97682009062544ff9aca66e056a069e6', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 14:49:04', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('978fbe2562c74687b81cbbf3d3a9e38a', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:25:31', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('98d89546e2054d999da2af0308360b6c', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:29:31', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('99848c0bd9874f22b24290b7f03e1679', '1', '系统设置-机构用户-用户管理-修改', '1', '2016-11-03 16:36:49', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/save', 'POST', 'id=&photo=/iLife/userfiles/1/images/photo/2016/11/4c69eb5b0d4761b6da06a22e3d950f06.jpg&company.id=54d05cbfef4f4bafb39ec67d0d26211c&company.name=PCI&office.id=56c6fb3511a548c09fb0964b00b2145c&office.name=测试组&no=003&name=chenci&oldLoginName=&loginName=chenci&newPassword=&confirmNewPassword=&email=&phone=&mobile=&loginFlag=1&userType=1&roleIdList=2&_roleIdList=on&remarks=', '');
INSERT INTO `sys_log` VALUES ('9a49cb084150478b9bb6e988616a45f1', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 16:49:55', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('9ad93f94c37544f083c9c6bb55969ae9', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:07:17', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('9b296f79b91349a0adc1b1479be2ea6a', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:17:39', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('9d79586734ef40d7bb76ccb42d9f0e37', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:26:13', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('9e79e79d95cf4d17aaf3e2136ed6980c', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 17:07:16', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('9efe5a3a5e314e408b241a8135edd392', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:28:38', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/form', 'GET', 'parent.id=', '');
INSERT INTO `sys_log` VALUES ('9f021e5e31454a48bbf94792663b623f', '1', '系统设置-机构用户-机构管理-修改', '1', '2016-11-03 16:26:07', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/delete', 'GET', 'id=1', '');
INSERT INTO `sys_log` VALUES ('9f0820e439ba404c912f800eb5b5a9f2', '1', '内容管理-内容管理', '1', '2016-11-03 14:08:54', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('9f2b65c3e13c4117a0c229a338e68c10', '1', '系统设置-系统设置-字典管理', '1', '2016-11-03 16:38:59', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/dict/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('9f8a0ce8c481401188827eb38c131094', '1', '系统设置-机构用户-用户管理-修改', '1', '2016-11-03 16:32:48', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/checkLoginName', 'GET', 'oldLoginName=admin&loginName=admin', '');
INSERT INTO `sys_log` VALUES ('a026a4d0bafd4d499009fa62fc9af7f3', '1', '系统设置-机构用户-用户管理-修改', '1', '2016-11-03 16:36:04', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/save', 'POST', 'id=&photo=/iLife/userfiles/1/images/photo/2016/11/4c69eb5b0d4761b6da06a22e3d950f06.jpg&company.id=54d05cbfef4f4bafb39ec67d0d26211c&company.name=PCI&office.id=56c6fb3511a548c09fb0964b00b2145c&office.name=测试组&no=002&name=审核人2&oldLoginName=&loginName=shenheren2&newPassword=&confirmNewPassword=&email=&phone=&mobile=&loginFlag=1&userType=1&roleIdList=2&_roleIdList=on&remarks=', '');
INSERT INTO `sys_log` VALUES ('a2763716e65b4c9994f489a7e6c5d018', '1', '我的面板-个人信息-修改密码', '1', '2016-11-03 16:03:38', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/modifyPwd', 'GET', 'tabPageId=jerichotabiframe_1', '');
INSERT INTO `sys_log` VALUES ('a2a44dc6d37d418db874348afcdb1ec0', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 16:39:29', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('a32f0088d1e34271b21f23266d1216c1', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:03:19', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('a3e4d784129d4ca5b5049d88e5d4e813', '1', '内容管理-内容管理-公共留言-查看', '1', '2016-11-03 13:46:12', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/guestbook/', 'GET', 'status=2', '');
INSERT INTO `sys_log` VALUES ('a4699e7f6d414ed2b3aa97452d83f0c9', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:32:05', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/form', 'GET', 'parent.id=', '');
INSERT INTO `sys_log` VALUES ('a4866b322c2a4d689ff31a0363dced2f', '1', '系统设置-日志查询-日志查询', '1', '2016-11-03 16:43:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/log', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('a4c18fe324b64c11becd6e1868d7fcc9', '1', '系统设置-系统设置-角色管理', '1', '2016-11-03 16:36:55', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('a52d6bae583548e88b8c5d813dcb200f', '1', '内容管理-栏目设置-站点设置', '1', '2016-11-03 14:48:58', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/site/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('a538292330f44db2a162351948f6edb4', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 16:39:41', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('a565b209b0574ddf8424a64c2bd5a415', '1', '内容管理-内容管理-评论管理-查看', '1', '2016-11-03 13:46:10', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/comment/', 'GET', 'status=2', '');
INSERT INTO `sys_log` VALUES ('a61b9d9d65b34babb639c38f4e7e5fd6', '1', '系统设置-系统设置-角色管理-查看', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:42:32', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/form', 'GET', 'id=2', '');
INSERT INTO `sys_log` VALUES ('a64eb8243fe94057b141be8bf77b26e0', '1', '系统设置-机构用户-用户管理-修改', '1', '2016-11-03 16:36:42', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/checkLoginName', 'GET', 'oldLoginName=&loginName=chenci', '');
INSERT INTO `sys_log` VALUES ('a6d68fb505804e6694d257281731a26b', '1', '系统设置-机构用户-机构管理', '1', '2016-11-03 16:26:16', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('a7614c54536f4ffaa2969df54f556990', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:29:44', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('a81bbb9b308f4d979a9581f6f9a48f11', '1', '系统设置-机构用户-机构管理-修改', '1', '2016-11-03 16:29:11', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/save', 'POST', 'id=&parent.id=&parent.name=&area.id=2c2948e6cb0144debe50903c44a097fa&area.name=成都&name=PCI开发&code=&type=2&grade=1&useable=1&primaryPerson.id=&office.primaryPerson.name=&deputyPerson.id=&office.deputyPerson.name=&address=&zipCode=&master=&phone=&fax=&email=&remarks=&childDeptList=2&_childDeptList=on', '');
INSERT INTO `sys_log` VALUES ('a8f05c67b54a40d4832c9d2d402fd3cf', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 13:47:40', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('a928104abf4d4c2aa5be54e1ea5a1dde', '1', '内容管理-内容管理', '1', '2016-11-03 14:13:35', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('a93daec4500649e6a60df7213fde3849', '1', '内容管理-栏目设置-栏目管理-修改', '1', '2016-11-03 14:08:57', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/updateSort', 'POST', '', '');
INSERT INTO `sys_log` VALUES ('a98d53e76c0d466f8654df5c2e6bf035', '1', '系统设置-系统设置-角色管理-查看', '1', '2016-11-03 16:37:10', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/form', 'GET', 'id=2', '');
INSERT INTO `sys_log` VALUES ('a9ea1f338a154319a907a4702b226489', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:48:28', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('a9f1a77c85ff4c1fb6bfd1c7b231c56c', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 14:48:22', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('aa57078d724e4a849fc664f76847d5ca', '1', '系统设置-系统设置-角色管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:36', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('ab3533167bca4f758260835a2ae4a7c1', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:22:23', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('abfd515f1e4443888238398806858c45', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:29:53', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/form', 'GET', 'parent.id=', '');
INSERT INTO `sys_log` VALUES ('ac5e6564ad0241b9b7f9849c135f2401', '1', '系统登录', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:41:17', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a', 'GET', 'login=', '');
INSERT INTO `sys_log` VALUES ('aec710922cf84be5b100ee7670e2da0c', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 14:07:53', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('aee1210cca244f59a13b1ee4c5b84c98', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:12:17', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b05463f1e30e48b88a73f67be4accbd3', '1', '内容管理-内容管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 17:21:50', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b0efa94b930142afb80af4705c1eb137', '1', '系统设置-机构用户-机构管理', '1', '2016-11-03 16:27:39', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b0f2720d5c4f44788444d78601eaf00a', '1', '系统设置-系统设置-角色管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:42:40', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', 'repage=', '');
INSERT INTO `sys_log` VALUES ('b105e510fd6c4cb5bb2f8eec154395b0', '1', '系统设置-系统设置-字典管理', '1', '2016-11-03 14:05:02', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/dict/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b126a4d5d5f5425ea45ecc1775bcb8d5', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:30:49', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b1c788359cdc4232a40afe13d38c64e6', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:26:07', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=0&parentIds=0,', '');
INSERT INTO `sys_log` VALUES ('b1ff8227b0dc4206949e83ba81a3e4dd', '1', '系统设置-机构用户-机构管理', '1', '2016-11-03 16:29:48', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b213ce3a72ce45f6b986125bcc125de4', '1', '系统设置-系统设置-菜单管理', '1', '2016-11-03 16:36:10', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/menu/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b21b1a5a9ed7433f8ab3ddaae8ec6f9a', '1', '系统设置-系统设置-字典管理', '1', '2016-11-03 14:05:06', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/dict/', 'POST', 'pageNo=2&pageSize=30&type=&description=', '');
INSERT INTO `sys_log` VALUES ('b2404eb3badf450494b11135bd414e70', '1', '系统设置-机构用户-机构管理', '1', '2016-11-03 16:25:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b25fe15fd6954b02b1f1f7274d9bf11b', '1', '内容管理-内容管理', '1', '2016-11-03 14:50:12', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b346a0f605c142209d7dc720bbf3fe0a', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 14:07:54', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b404cebecd4b4c8ea83f41ba83aa5c76', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 16:46:34', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b5b77bf521054b908991b6232137667b', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:49:01', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b6e8d50f03a1405f9b66d82d61c4d742', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 17:07:34', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b728899b7c134d9faef59ab97ccfcf96', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 16:39:33', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b79647c22e974a0593d88fe27a21892d', '1', '内容管理-内容管理', '1', '2016-11-03 16:39:29', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b7de7308896d41e8a5335568cedd0b0e', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:42:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b7fd2f4cacb9463eb6d2a28f692273cd', '1', '内容管理-内容管理-评论管理-查看', '1', '2016-11-03 16:39:44', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/comment/', 'GET', 'status=2', '');
INSERT INTO `sys_log` VALUES ('b8189952e37547d5817989dc00e480e1', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 13:59:43', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b8a0ed675d0d4fe8b2d598f8df03c678', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 16:39:48', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('b93310af6e9b489894c5626b7da5d2b7', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 16:47:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('bac7ecfb656a4eaca9e43045d40de7f3', '1', '内容管理-内容管理', '1', '2016-11-03 14:07:36', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('bbe7c6757d404636876bcca34cc657ab', '1', '内容管理-栏目设置-栏目管理-查看', '1', '2016-11-03 14:12:38', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('bd21c6d19de8475a9d61aded2251d56e', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:36:49', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', 'repage=', '');
INSERT INTO `sys_log` VALUES ('bd67a2ecd8d544fe8963cf45b2bcb1df', '1', '内容管理-内容管理-内容发布-文章模型', '1', '2016-11-03 14:50:19', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/article/', 'GET', 'category.id=4', '');
INSERT INTO `sys_log` VALUES ('be03cae5997c420686e8766952c925d2', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 17:28:16', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('be3fc057150545a391cd107e3ea2f3f6', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 14:19:45', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('be7f0bc11e3a402b8ca2595bfedfd02a', '1', '系统登录', '1', '2016-11-03 16:52:58', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/WEB-INF/views/modules/act/actTaskTodoList.jsp', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('bf135e261e5a41dfbee7dbd9289a5baf', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:26:17', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('bf3a47a923d44b0ea1f0616df2fe9435', '1', '内容管理-内容管理', '1', '2016-11-03 16:39:29', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('bf7645df8b214100aee0c25edc240c4e', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:29:03', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('bfa3fedfddb14639ae11d842f7e0cca8', '1', '内容管理-内容管理', '1', '2016-11-03 16:39:48', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('c073e95c978849be87def02a0f62d16d', '1', '内容管理-内容管理', '1', '2016-11-03 13:46:09', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('c08104c97ee7461f9477649e1ea464a0', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:29:11', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=0,', '');
INSERT INTO `sys_log` VALUES ('c0ad02b830bc4e1e950ec94d633bf52a', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 16:46:32', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('c1a981f94770486b8fc95464b33710c2', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:02:59', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('c1c55c3b696e40d6809a36e6273167e2', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:33:13', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', 'office.id=56c6fb3511a548c09fb0964b00b2145c&office.name=测试组', '');
INSERT INTO `sys_log` VALUES ('c24904eccc31468f8cca9a83c30eca94', '1', '在线办公-流程管理-模型管理', '1', '2016-11-03 13:54:16', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/model', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('c3a34913cfc84074a42de8408c009eac', '1', '内容管理-内容管理', '1', '2016-11-03 13:46:09', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('c4816c58be3c47ac82c126a150d1724d', '1', '内容管理-内容管理', '1', '2016-11-03 13:46:13', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('c5f7505b20914ee6983b16210a1d3f7d', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 16:47:45', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('c6af5d4e737642b38116324d3871ea7a', '1', '内容管理-栏目设置-栏目管理-查看', '1', '2016-11-03 14:09:13', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('c866c8005e1d4651bd734649ba8266d3', '1', '系统设置-机构用户-用户管理-修改', '1', '2016-11-03 16:35:26', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/save', 'POST', 'id=&photo=/iLife/userfiles/1/images/photo/2016/11/4c69eb5b0d4761b6da06a22e3d950f06.jpg&company.id=54d05cbfef4f4bafb39ec67d0d26211c&company.name=PCI&office.id=56c6fb3511a548c09fb0964b00b2145c&office.name=测试组&no=001&name=审核人1&oldLoginName=&loginName=shenheren1&newPassword=&confirmNewPassword=&email=&phone=&mobile=&loginFlag=1&userType=1&roleIdList=2&_roleIdList=on&remarks=', '');
INSERT INTO `sys_log` VALUES ('ca53e5a0fb1647f180370e244b5110e4', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 16:46:39', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('ca64fd2f0c0f4b3ab6420d499ae0a482', '1', '在线办公-通知通告-我的通告', '1', '2016-11-03 16:03:55', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('cadefc8327114a4c88d02549986289e9', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:12:26', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('cae5b091feac4bca8b31e326c0dd1eb5', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:29:15', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=54d05cbfef4f4bafb39ec67d0d26211c&parentIds=0,54d05cbfef4f4bafb39ec67d0d26211c,', '');
INSERT INTO `sys_log` VALUES ('cb832bec612d42b58c548265f7efe161', '1', '内容管理-内容管理-内容发布', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 17:21:50', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('ccabeb5f9b2a4f24a02d52e7bb024614', '1', '系统登录', '1', '2016-11-03 16:25:06', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a', 'GET', 'login=', '');
INSERT INTO `sys_log` VALUES ('cda26bc7604649ef903e4bb00dc2db20', '1', '系统登录', '1', '2016-11-03 13:46:03', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a', 'GET', 'login=', '');
INSERT INTO `sys_log` VALUES ('cdf39d47b6db48508958be010bccc04f', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 16:36:19', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('cf1449bb14aa436dbc1885d0bc144dfe', '1', '系统登录', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 17:21:46', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a', 'GET', 'login=', '');
INSERT INTO `sys_log` VALUES ('cf4111aff5b64dbebff3e6ee63758dc6', '1', '内容管理-内容管理-内容发布-文章模型', '1', '2016-11-03 14:50:20', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/article/', 'GET', 'category.id=5', '');
INSERT INTO `sys_log` VALUES ('cf68a0e4a8d84559afa7f13baa598f4e', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 16:49:54', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d25478235953433b82a1d1e668d628c6', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:28:12', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d254a9605b4c41b2be5f42e9c006bdc2', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 16:46:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d2b56f87a72c49aab4aa80d668749b01', '1', '系统设置-系统设置-角色管理', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:42:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d2c1a3fe3bdf431fb7275bdb48f6aa9c', '1', '系统登录', '1', '2016-11-03 15:50:22', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a', 'GET', 'login=', '');
INSERT INTO `sys_log` VALUES ('d3553dec1a2e46a6874f27c611f815d4', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 16:26:22', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d4b5d47d6f3d4dc183caa9cf6f93d2ab', '1', '内容管理-栏目设置-站点设置', '1', '2016-11-03 14:07:41', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/site/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d4f0cd5b4b6244cc82fa9bf114b96f71', '1', '内容管理-内容管理', '1', '2016-11-03 14:50:12', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d58be758c092413aafcb9b4476b0f6de', '1', '内容管理-栏目设置-站点设置-查看', '1', '2016-11-03 14:07:45', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/site/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d605398520c34d839d79bac12017e204', '1', '系统登录', '1', '2016-11-03 13:57:35', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process/resource/read', 'GET', 'procDefId=test_audit:1:0b4f357fa3a14499af03d0f6bd8ba230&resType=xml', '');
INSERT INTO `sys_log` VALUES ('d68c875d7f144181ae9e74a246bcd962', '1', '系统登录', '1', '2016-11-03 14:26:33', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit/form', 'GET', 'act.taskId=&act.taskName=&act.taskDefKey=&act.procInsId=&act.procDefId=test_audit:1:0b4f357fa3a14499af03d0f6bd8ba230&act.status=&id=', '');
INSERT INTO `sys_log` VALUES ('d704248dcf5442f4982cddead6f39a8e', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 13:49:29', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d7b7afad9f4346559d81d410a0b1a784', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:26:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d7fd9a364c094bf8978c06fdd5102b67', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:06:03', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d810525a49a740a9b6c4c0057e59109c', '1', '在线办公-流程管理-模型管理', '1', '2016-11-03 14:19:22', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/model', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d990777347c841c2a92fe11c7b221de8', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 13:47:43', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('d9e8667d58b94994967b9bbda090dbcb', '1', '在线办公-流程管理-模型管理', '1', '2016-11-03 13:49:14', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/model', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('dab4797a693648bc9b71cdd8ef821eae', '1', '内容管理-内容管理', '1', '2016-11-03 14:48:22', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('dbee1f7dede5421db54b8f94fc52a68a', '1', '系统设置-系统设置-角色管理-查看', '295def53ef614f06b164f78c4944eb4d', '2016-11-03 16:42:28', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/form', 'GET', 'id=3', '');
INSERT INTO `sys_log` VALUES ('dbfbd083b57c483883d241ca88697107', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 15:39:15', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('dd5031da2e2a45ef9457eb5675a47ff4', '1', '内容管理-内容管理-内容发布', '1', '2016-11-03 14:19:16', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('dde360973c67455c82fadee6d95d7ea7', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:29:54', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('dde8da6233894b4aa44e4f084a0529b7', '1', '在线办公-通知通告-我的通告', '1', '2016-11-03 13:48:23', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('dfa54b8f15ee4a88b6a06348bc75d8da', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 14:47:30', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e0baec5948764ac08cebf617290b3671', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 14:20:17', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e1e6e2441e144a8e8ada058193667d66', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:03:16', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e25038d7a49e4688b54d17e959b5c57e', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 15:53:25', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e271176717b44314bbc212bcc4ba9a1f', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:28:36', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=', '');
INSERT INTO `sys_log` VALUES ('e27839245b0f4ee3b45248104cbae006', '1', '系统设置-机构用户-用户管理-修改', '1', '2016-11-03 16:25:40', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/delete', 'GET', 'id=2', '');
INSERT INTO `sys_log` VALUES ('e282846eee0b4abfa0fee793ad33d739', '1', '内容管理-内容管理', '1', '2016-11-03 14:49:04', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/none', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e4ecd066eb444049a109760778c3dfdf', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:03:46', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e4f8dedf834e49f2aa23bbca6c74aff3', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 14:48:25', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e6b96fbd0fc14838a36e9c00646e4515', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 17:28:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e6c1827307bf476b98c5bf87654b3b9f', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:26:16', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=', '');
INSERT INTO `sys_log` VALUES ('e6c8c181fd064dbca1ac56b31fbb1e2e', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 13:47:20', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'POST', 'photo=/iLife/userfiles/1/images/photo/2016/11/4c69eb5b0d4761b6da06a22e3d950f06.jpg&name=系统管理员&email=&phone=8675&mobile=8675&remarks=最高管理员', '');
INSERT INTO `sys_log` VALUES ('e6ccdd1dfb0b45f0bb005a3f8b65356a', '1', '系统设置-系统设置-角色管理', '1', '2016-11-03 16:43:23', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e6e768a69fe649ab93f4338c21b6d2d2', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:25:57', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=', '');
INSERT INTO `sys_log` VALUES ('e7dd8d7fa87548d78b45bbbc70f3cca5', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 13:46:14', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e8343c18505249279ce46fc7c94bc4b3', '1', '系统设置-系统设置-字典管理', '1', '2016-11-03 16:36:59', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/dict/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e87a2233d03e4be3b2fac6297edb80b4', '1', '内容管理-内容管理', '1', '2016-11-03 16:44:18', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/tree', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e8f6ecff22d648429db95a3e04ed8e6c', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 16:36:23', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('e9ff8ceb19774f929a5840b25f8f46ed', '1', '在线办公-流程管理-模型管理', '1', '2016-11-03 13:48:32', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/model', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('eae4e8641af9408c9d74eb31e833d288', '1', '内容管理-栏目设置-栏目管理-查看', '1', '2016-11-03 14:09:08', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('ee854b995ef94acfa2c5199b538c43fd', '1', '系统设置-机构用户-机构管理', '1', '2016-11-03 16:25:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('ef2aa879c1104d799da5f95ba90ac4fc', '1', '在线办公-通知通告-我的通告', '1', '2016-11-03 16:04:28', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('ef40850f5293448cb0200feda7b8cf35', '1', '我的面板-个人信息-个人信息', '1', '2016-11-03 16:03:36', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/info', 'GET', 'tabPageId=jerichotabiframe_0', '');
INSERT INTO `sys_log` VALUES ('ef823d51779845c6bb4a042cc91494e7', '1', '系统登录', '1', '2016-11-03 14:13:31', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a', 'GET', 'login=', '');
INSERT INTO `sys_log` VALUES ('efbd1fab77674f63a3735dc3d297624b', '1', '系统设置-机构用户-机构管理', '1', '2016-11-03 16:28:36', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('f3e0396d09a54440a0cae42046b949bf', '1', '在线办公-个人办公-审批测试', '1', '2016-11-03 17:13:03', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('f4e1ecedbfa647e5b63334c739b47514', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 16:26:13', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('f50b4cfc7cdb4b01ad879c274152924e', '1', '在线办公-通知通告-我的通告', '1', '2016-11-03 16:44:17', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/oaNotify/self', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('f695a131f1dd42b0a21a5dcf50bc97ca', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 13:49:40', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('f828bf835eeb40da82fcf4b6c8d2b977', '1', '内容管理-栏目设置-栏目管理-查看', '1', '2016-11-03 14:19:01', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/form', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('f93d54882dec4ad0a6b9fc2860f4c386', '1', '系统设置-机构用户-用户管理-查看', '1', '2016-11-03 14:04:58', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/list', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('fa01068288ec43f2b88d884dbaf41e6d', '1', '系统设置-机构用户-机构管理', '1', '2016-11-03 16:27:55', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('fa1e53b5e12743cfbd35e0e7dbf6a462', '1', '在线办公-个人办公-我的任务', '1', '2016-11-03 14:47:22', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/task/todo/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('fa2827449fbd47ddaae47a5ad95d3fe5', '1', '系统设置-系统设置-角色管理', '1', '2016-11-03 16:43:27', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/role/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('fa58f039e0024d14a49cc5e19f2572c8', '1', '系统登录', '1', '2016-11-03 14:40:54', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit/form', 'GET', 'act.taskId=&act.taskName=&act.taskDefKey=&act.procInsId=&act.procDefId=test_audit:1:0b4f357fa3a14499af03d0f6bd8ba230&act.status=&id=', '');
INSERT INTO `sys_log` VALUES ('faa00c3f7d614c1389e5d24e3c9e6a98', '1', '系统登录', '1', '2016-11-03 15:53:24', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a', 'GET', 'login=', '');
INSERT INTO `sys_log` VALUES ('fb03eb2cc71f45c3aa517dee7e71385a', '1', '内容管理-栏目设置-切换站点', '1', '2016-11-03 14:49:00', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/site/select', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('fc6e892a3a584b35b9ae0afe456c40eb', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:27:39', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/list', 'GET', 'id=&parentIds=', '');
INSERT INTO `sys_log` VALUES ('fcd3b4419f6e4c45970f6971025f1e97', '1', '系统登录', '1', '2016-11-03 17:28:11', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/oa/testAudit/save', 'POST', 'id=&act.taskId=&act.taskName=&act.taskDefKey=&act.procInsId=&act.procDefId=&act.flag=yes&user.id=295def53ef614f06b164f78c4944eb4d&user.name=审核人1&office.id=56c6fb3511a548c09fb0964b00b2145c&office.name=测试组&post=&content=1&olda=1&newa=2&oldb=3&newb=4&oldc=5&newc=6&addNum=1&exeDate=2', '');
INSERT INTO `sys_log` VALUES ('fd684dd0aaa84f72967882d902d666bc', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 13:48:33', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('fea0f00911e149a3a79a34d3b3ecca21', '1', '系统登录', '1', '2016-11-03 17:12:56', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/WEB-INF/views/modules/act/actTaskTodoList.jsp', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('ff2de802b44d42a8b8d15d509095956b', '1', '系统设置-机构用户-机构管理-查看', '1', '2016-11-03 16:31:07', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/office/form', 'GET', 'id=54d05cbfef4f4bafb39ec67d0d26211c', '');
INSERT INTO `sys_log` VALUES ('ff332c938c68469880f9891239b662af', '1', '内容管理-栏目设置-栏目管理', '1', '2016-11-03 15:39:20', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/cms/category/', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('ff844bbb8f094f8fa391082942dfa672', '1', '系统设置-机构用户-用户管理', '1', '2016-11-03 14:04:57', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/sys/user/index', 'GET', '', '');
INSERT INTO `sys_log` VALUES ('ffab31dd2c4341bdb7b74fa9d6e70733', '1', '在线办公-流程管理-流程管理', '1', '2016-11-03 14:06:12', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36', '/iLife/a/act/process', 'GET', '', '');

-- ----------------------------
-- Table structure for sys_mdict
-- ----------------------------
DROP TABLE IF EXISTS `sys_mdict`;
CREATE TABLE `sys_mdict` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `parent_id` varchar(64) NOT NULL COMMENT '父级编号',
  `parent_ids` varchar(2000) NOT NULL COMMENT '所有父级编号',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `sort` decimal(10,0) NOT NULL COMMENT '排序',
  `description` varchar(100) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(64) NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `sys_mdict_parent_id` (`parent_id`),
  KEY `sys_mdict_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='多级字典表';

-- ----------------------------
-- Records of sys_mdict
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `parent_id` varchar(64) NOT NULL COMMENT '父级编号',
  `parent_ids` varchar(2000) NOT NULL COMMENT '所有父级编号',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `sort` decimal(10,0) NOT NULL COMMENT '排序',
  `href` varchar(2000) DEFAULT NULL COMMENT '链接',
  `target` varchar(20) DEFAULT NULL COMMENT '目标',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `is_show` char(1) NOT NULL COMMENT '是否在菜单中显示',
  `permission` varchar(200) DEFAULT NULL COMMENT '权限标识',
  `create_by` varchar(64) NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `sys_menu_parent_id` (`parent_id`),
  KEY `sys_menu_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='菜单表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '0', '0,', '功能菜单', '0', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('10', '3', '0,1,2,3,', '字典管理', '60', '/sys/dict/', '', 'th-list', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('11', '10', '0,1,2,3,10,', '查看', '30', '', '', '', '0', 'sys:dict:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('12', '10', '0,1,2,3,10,', '修改', '40', '', '', '', '0', 'sys:dict:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('13', '2', '0,1,2,', '机构用户', '970', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('14', '13', '0,1,2,13,', '区域管理', '50', '/sys/area/', '', 'th', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('15', '14', '0,1,2,13,14,', '查看', '30', '', '', '', '0', 'sys:area:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('16', '14', '0,1,2,13,14,', '修改', '40', '', '', '', '0', 'sys:area:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('17', '13', '0,1,2,13,', '机构管理', '40', '/sys/office/', '', 'th-large', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('18', '17', '0,1,2,13,17,', '查看', '30', '', '', '', '0', 'sys:office:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('19', '17', '0,1,2,13,17,', '修改', '40', '', '', '', '0', 'sys:office:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('2', '1', '0,1,', '系统设置', '900', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('20', '13', '0,1,2,13,', '用户管理', '30', '/sys/user/index', '', 'user', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('21', '20', '0,1,2,13,20,', '查看', '30', '', '', '', '0', 'sys:user:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('22', '20', '0,1,2,13,20,', '修改', '40', '', '', '', '0', 'sys:user:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('23', '2', '0,1,2,', '关于帮助', '990', '', '', '', '0', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('24', '23', '0,1,2,23', '官方首页', '30', 'http://iLife.com', '_blank', '', '0', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('25', '23', '0,1,2,23', '项目支持', '50', 'http://iLife.com/donation.html', '_blank', '', '0', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '1');
INSERT INTO `sys_menu` VALUES ('26', '23', '0,1,2,23', '论坛交流', '80', 'http://bbs.iLife.com', '_blank', '', '0', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '1');
INSERT INTO `sys_menu` VALUES ('27', '1', '0,1,', '我的面板', '100', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('28', '27', '0,1,27,', '个人信息', '30', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('29', '28', '0,1,27,28,', '个人信息', '30', '/sys/user/info', '', 'user', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('3', '2', '0,1,2,', '系统设置', '980', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('30', '28', '0,1,27,28,', '修改密码', '40', '/sys/user/modifyPwd', '', 'lock', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('31', '1', '0,1,', '内容管理', '500', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('32', '31', '0,1,31,', '栏目设置', '990', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('33', '32', '0,1,31,32', '栏目管理', '30', '/cms/category/', '', 'align-justify', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('34', '33', '0,1,31,32,33,', '查看', '30', '', '', '', '0', 'cms:category:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('35', '33', '0,1,31,32,33,', '修改', '40', '', '', '', '0', 'cms:category:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('36', '32', '0,1,31,32', '站点设置', '40', '/cms/site/', '', 'certificate', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('37', '36', '0,1,31,32,36,', '查看', '30', '', '', '', '0', 'cms:site:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('38', '36', '0,1,31,32,36,', '修改', '40', '', '', '', '0', 'cms:site:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('39', '32', '0,1,31,32', '切换站点', '50', '/cms/site/select', '', 'retweet', '1', 'cms:site:select', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('4', '3', '0,1,2,3,', '菜单管理', '30', '/sys/menu/', '', 'list-alt', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('40', '31', '0,1,31,', '内容管理', '500', '', '', '', '1', 'cms:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('41', '40', '0,1,31,40,', '内容发布', '30', '/cms/', '', 'briefcase', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('42', '41', '0,1,31,40,41,', '文章模型', '40', '/cms/article/', '', 'file', '0', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('43', '42', '0,1,31,40,41,42,', '查看', '30', '', '', '', '0', 'cms:article:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('44', '42', '0,1,31,40,41,42,', '修改', '40', '', '', '', '0', 'cms:article:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('45', '42', '0,1,31,40,41,42,', '审核', '50', '', '', '', '0', 'cms:article:audit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('46', '41', '0,1,31,40,41,', '链接模型', '60', '/cms/link/', '', 'random', '0', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('47', '46', '0,1,31,40,41,46,', '查看', '30', '', '', '', '0', 'cms:link:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('48', '46', '0,1,31,40,41,46,', '修改', '40', '', '', '', '0', 'cms:link:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('49', '46', '0,1,31,40,41,46,', '审核', '50', '', '', '', '0', 'cms:link:audit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('5', '4', '0,1,2,3,4,', '查看', '30', '', '', '', '0', 'sys:menu:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('50', '40', '0,1,31,40,', '评论管理', '40', '/cms/comment/?status=2', '', 'comment', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('51', '50', '0,1,31,40,50,', '查看', '30', '', '', '', '0', 'cms:comment:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('52', '50', '0,1,31,40,50,', '审核', '40', '', '', '', '0', 'cms:comment:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('53', '40', '0,1,31,40,', '公共留言', '80', '/cms/guestbook/?status=2', '', 'glass', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('54', '53', '0,1,31,40,53,', '查看', '30', '', '', '', '0', 'cms:guestbook:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('55', '53', '0,1,31,40,53,', '审核', '40', '', '', '', '0', 'cms:guestbook:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('56', '71', '0,1,27,71,', '文件管理', '90', '/../static/ckfinder/ckfinder.html', '', 'folder-open', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('57', '56', '0,1,27,40,56,', '查看', '30', '', '', '', '0', 'cms:ckfinder:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('58', '56', '0,1,27,40,56,', '上传', '40', '', '', '', '0', 'cms:ckfinder:upload', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('59', '56', '0,1,27,40,56,', '修改', '50', '', '', '', '0', 'cms:ckfinder:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('6', '4', '0,1,2,3,4,', '修改', '40', '', '', '', '0', 'sys:menu:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('60', '31', '0,1,31,', '统计分析', '600', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('61', '60', '0,1,31,60,', '信息量统计', '30', '/cms/stats/article', '', 'tasks', '1', 'cms:stats:article', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('62', '1', '0,1,', '在线办公', '200', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('63', '62', '0,1,62,', '个人办公', '30', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('64', '63', '0,1,62,63,', '请假办理', '300', '/oa/leave', '', 'leaf', '0', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('65', '64', '0,1,62,63,64,', '查看', '30', '', '', '', '0', 'oa:leave:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('66', '64', '0,1,62,63,64,', '修改', '40', '', '', '', '0', 'oa:leave:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('67', '2', '0,1,2,', '日志查询', '985', '', '', '', '1', '', '1', '2013-06-03 00:00:00', '1', '2013-06-03 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('68', '67', '0,1,2,67,', '日志查询', '30', '/sys/log', '', 'pencil', '1', 'sys:log:view', '1', '2013-06-03 00:00:00', '1', '2013-06-03 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('69', '62', '0,1,62,', '流程管理', '300', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('7', '3', '0,1,2,3,', '角色管理', '50', '/sys/role/', '', 'lock', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('70', '69', '0,1,62,69,', '流程管理', '50', '/act/process', '', 'road', '1', 'act:process:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('71', '27', '0,1,27,', '文件管理', '90', '', '', '', '1', '', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('72', '69', '0,1,62,69,', '模型管理', '100', '/act/model', '', 'road', '1', 'act:model:edit', '1', '2013-09-20 00:00:00', '1', '2013-09-20 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('73', '63', '0,1,62,63,', '我的任务', '50', '/act/task/todo/', '', 'tasks', '1', '', '1', '2013-09-24 00:00:00', '1', '2013-09-24 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('74', '63', '0,1,62,63,', '审批测试', '100', '/oa/testAudit', '', '', '1', 'oa:testAudit:view,oa:testAudit:edit', '1', '2013-09-24 00:00:00', '1', '2013-09-24 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('75', '1', '0,1,', '在线演示', '3000', '', '', '', '1', '', '1', '2013-10-08 00:00:00', '1', '2013-10-08 00:00:00', '', '1');
INSERT INTO `sys_menu` VALUES ('79', '1', '0,1,', '代码生成', '5000', '', '', '', '1', '', '1', '2013-10-16 00:00:00', '1', '2013-10-16 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('8', '7', '0,1,2,3,7,', '查看', '30', '', '', '', '0', 'sys:role:view', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('80', '79', '0,1,79,', '代码生成', '50', '', '', '', '1', '', '1', '2013-10-16 00:00:00', '1', '2013-10-16 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('81', '80', '0,1,79,80,', '生成方案配置', '30', '/gen/genScheme', '', '', '1', 'gen:genScheme:view,gen:genScheme:edit', '1', '2013-10-16 00:00:00', '1', '2013-10-16 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('82', '80', '0,1,79,80,', '业务表配置', '20', '/gen/genTable', '', '', '1', 'gen:genTable:view,gen:genTable:edit,gen:genTableColumn:view,gen:genTableColumn:edit', '1', '2013-10-16 00:00:00', '1', '2013-10-16 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('83', '80', '0,1,79,80,', '代码模板管理', '90', '/gen/genTemplate', '', '', '1', 'gen:genTemplate:view,gen:genTemplate:edit', '1', '2013-10-16 00:00:00', '1', '2013-10-16 00:00:00', '', '1');
INSERT INTO `sys_menu` VALUES ('84', '67', '0,1,2,67,', '连接池监视', '40', '/../druid', '', '', '1', '', '1', '2013-10-18 00:00:00', '1', '2013-10-18 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('85', '76', '0,1,75,76,', '行政区域', '80', '/../static/map/map-city.html', '', '', '1', '', '1', '2013-10-22 00:00:00', '1', '2013-10-22 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('86', '75', '0,1,75,', '组件演示', '50', '', '', '', '1', '', '1', '2013-10-22 00:00:00', '1', '2013-10-22 00:00:00', '', '1');
INSERT INTO `sys_menu` VALUES ('87', '86', '0,1,75,86,', '组件演示', '30', '/test/test/form', '', '', '1', 'test:test:view,test:test:edit', '1', '2013-10-22 00:00:00', '1', '2013-10-22 00:00:00', '', '1');
INSERT INTO `sys_menu` VALUES ('88', '62', '0,1,62,', '通知通告', '20', '', '', '', '1', '', '1', '2013-11-08 00:00:00', '1', '2013-11-08 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('89', '88', '0,1,62,88,', '我的通告', '30', '/oa/oaNotify/self', '', '', '1', '', '1', '2013-11-08 00:00:00', '1', '2013-11-08 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('9', '7', '0,1,2,3,7,', '修改', '40', '', '', '', '0', 'sys:role:edit', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_menu` VALUES ('90', '88', '0,1,62,88,', '通告管理', '50', '/oa/oaNotify', '', '', '1', 'oa:oaNotify:view,oa:oaNotify:edit', '1', '2013-11-08 00:00:00', '1', '2013-11-08 00:00:00', '', '0');

-- ----------------------------
-- Table structure for sys_office
-- ----------------------------
DROP TABLE IF EXISTS `sys_office`;
CREATE TABLE `sys_office` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `parent_id` varchar(64) NOT NULL COMMENT '父级编号',
  `parent_ids` varchar(2000) NOT NULL COMMENT '所有父级编号',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `sort` decimal(10,0) NOT NULL COMMENT '排序',
  `area_id` varchar(64) NOT NULL COMMENT '归属区域',
  `code` varchar(100) DEFAULT NULL COMMENT '区域编码',
  `type` char(1) NOT NULL COMMENT '机构类型',
  `grade` char(1) NOT NULL COMMENT '机构等级',
  `address` varchar(255) DEFAULT NULL COMMENT '联系地址',
  `zip_code` varchar(100) DEFAULT NULL COMMENT '邮政编码',
  `master` varchar(100) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(200) DEFAULT NULL COMMENT '电话',
  `fax` varchar(200) DEFAULT NULL COMMENT '传真',
  `email` varchar(200) DEFAULT NULL COMMENT '邮箱',
  `USEABLE` varchar(64) DEFAULT NULL COMMENT '是否启用',
  `PRIMARY_PERSON` varchar(64) DEFAULT NULL COMMENT '主负责人',
  `DEPUTY_PERSON` varchar(64) DEFAULT NULL COMMENT '副负责人',
  `create_by` varchar(64) NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `sys_office_parent_id` (`parent_id`),
  KEY `sys_office_del_flag` (`del_flag`),
  KEY `sys_office_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机构表';

-- ----------------------------
-- Records of sys_office
-- ----------------------------
INSERT INTO `sys_office` VALUES ('54d05cbfef4f4bafb39ec67d0d26211c', '0', '0,', 'PCI', '30', '2c2948e6cb0144debe50903c44a097fa', '', '1', '1', '', '', '', '', '', '', '1', '', '', '1', '2016-11-03 16:29:11', '1', '2016-11-03 16:31:25', '', '0');
INSERT INTO `sys_office` VALUES ('56c6fb3511a548c09fb0964b00b2145c', '54d05cbfef4f4bafb39ec67d0d26211c', '0,54d05cbfef4f4bafb39ec67d0d26211c,', '测试组', '30', '2c2948e6cb0144debe50903c44a097fa', '001', '2', '2', '', '', '', '', '', '', '1', '', '', '1', '2016-11-03 16:31:56', '1', '2016-11-03 16:31:56', '', '0');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `office_id` varchar(64) DEFAULT NULL COMMENT '归属机构',
  `name` varchar(100) NOT NULL COMMENT '角色名称',
  `enname` varchar(255) DEFAULT NULL COMMENT '英文名称',
  `role_type` varchar(255) DEFAULT NULL COMMENT '角色类型',
  `data_scope` char(1) DEFAULT NULL COMMENT '数据范围',
  `is_sys` varchar(64) DEFAULT NULL COMMENT '是否系统数据',
  `useable` varchar(64) DEFAULT NULL COMMENT '是否可用',
  `create_by` varchar(64) NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `sys_role_del_flag` (`del_flag`),
  KEY `sys_role_enname` (`enname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '54d05cbfef4f4bafb39ec67d0d26211c', '系统管理员', 'dept', 'assignment', '1', null, '1', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_role` VALUES ('2', '54d05cbfef4f4bafb39ec67d0d26211c', '公司管理员', 'hr', 'assignment', '2', null, '1', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_role` VALUES ('3', '54d05cbfef4f4bafb39ec67d0d26211c', '本公司管理员', 'a', 'assignment', '3', null, '1', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_role` VALUES ('4', '54d05cbfef4f4bafb39ec67d0d26211c', '部门管理员', 'b', 'assignment', '4', null, '1', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_role` VALUES ('5', '54d05cbfef4f4bafb39ec67d0d26211c', '本部门管理员', 'c', 'assignment', '5', null, '1', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_role` VALUES ('6', '54d05cbfef4f4bafb39ec67d0d26211c', '普通用户', 'd', 'assignment', '8', null, '1', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');
INSERT INTO `sys_role` VALUES ('7', '54d05cbfef4f4bafb39ec67d0d26211c', '济南市管理员', 'e', 'assignment', '9', null, '1', '1', '2013-05-27 00:00:00', '1', '2013-05-27 00:00:00', '', '0');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` varchar(64) NOT NULL COMMENT '角色编号',
  `menu_id` varchar(64) NOT NULL COMMENT '菜单编号',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色-菜单';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('1', '1');
INSERT INTO `sys_role_menu` VALUES ('1', '10');
INSERT INTO `sys_role_menu` VALUES ('1', '11');
INSERT INTO `sys_role_menu` VALUES ('1', '12');
INSERT INTO `sys_role_menu` VALUES ('1', '13');
INSERT INTO `sys_role_menu` VALUES ('1', '14');
INSERT INTO `sys_role_menu` VALUES ('1', '15');
INSERT INTO `sys_role_menu` VALUES ('1', '16');
INSERT INTO `sys_role_menu` VALUES ('1', '17');
INSERT INTO `sys_role_menu` VALUES ('1', '18');
INSERT INTO `sys_role_menu` VALUES ('1', '19');
INSERT INTO `sys_role_menu` VALUES ('1', '2');
INSERT INTO `sys_role_menu` VALUES ('1', '20');
INSERT INTO `sys_role_menu` VALUES ('1', '21');
INSERT INTO `sys_role_menu` VALUES ('1', '22');
INSERT INTO `sys_role_menu` VALUES ('1', '23');
INSERT INTO `sys_role_menu` VALUES ('1', '24');
INSERT INTO `sys_role_menu` VALUES ('1', '25');
INSERT INTO `sys_role_menu` VALUES ('1', '26');
INSERT INTO `sys_role_menu` VALUES ('1', '27');
INSERT INTO `sys_role_menu` VALUES ('1', '28');
INSERT INTO `sys_role_menu` VALUES ('1', '29');
INSERT INTO `sys_role_menu` VALUES ('1', '3');
INSERT INTO `sys_role_menu` VALUES ('1', '30');
INSERT INTO `sys_role_menu` VALUES ('1', '31');
INSERT INTO `sys_role_menu` VALUES ('1', '32');
INSERT INTO `sys_role_menu` VALUES ('1', '33');
INSERT INTO `sys_role_menu` VALUES ('1', '34');
INSERT INTO `sys_role_menu` VALUES ('1', '35');
INSERT INTO `sys_role_menu` VALUES ('1', '36');
INSERT INTO `sys_role_menu` VALUES ('1', '37');
INSERT INTO `sys_role_menu` VALUES ('1', '38');
INSERT INTO `sys_role_menu` VALUES ('1', '39');
INSERT INTO `sys_role_menu` VALUES ('1', '4');
INSERT INTO `sys_role_menu` VALUES ('1', '40');
INSERT INTO `sys_role_menu` VALUES ('1', '41');
INSERT INTO `sys_role_menu` VALUES ('1', '42');
INSERT INTO `sys_role_menu` VALUES ('1', '43');
INSERT INTO `sys_role_menu` VALUES ('1', '44');
INSERT INTO `sys_role_menu` VALUES ('1', '45');
INSERT INTO `sys_role_menu` VALUES ('1', '46');
INSERT INTO `sys_role_menu` VALUES ('1', '47');
INSERT INTO `sys_role_menu` VALUES ('1', '48');
INSERT INTO `sys_role_menu` VALUES ('1', '49');
INSERT INTO `sys_role_menu` VALUES ('1', '5');
INSERT INTO `sys_role_menu` VALUES ('1', '50');
INSERT INTO `sys_role_menu` VALUES ('1', '51');
INSERT INTO `sys_role_menu` VALUES ('1', '52');
INSERT INTO `sys_role_menu` VALUES ('1', '53');
INSERT INTO `sys_role_menu` VALUES ('1', '54');
INSERT INTO `sys_role_menu` VALUES ('1', '55');
INSERT INTO `sys_role_menu` VALUES ('1', '56');
INSERT INTO `sys_role_menu` VALUES ('1', '57');
INSERT INTO `sys_role_menu` VALUES ('1', '58');
INSERT INTO `sys_role_menu` VALUES ('1', '59');
INSERT INTO `sys_role_menu` VALUES ('1', '6');
INSERT INTO `sys_role_menu` VALUES ('1', '60');
INSERT INTO `sys_role_menu` VALUES ('1', '61');
INSERT INTO `sys_role_menu` VALUES ('1', '62');
INSERT INTO `sys_role_menu` VALUES ('1', '63');
INSERT INTO `sys_role_menu` VALUES ('1', '64');
INSERT INTO `sys_role_menu` VALUES ('1', '65');
INSERT INTO `sys_role_menu` VALUES ('1', '66');
INSERT INTO `sys_role_menu` VALUES ('1', '67');
INSERT INTO `sys_role_menu` VALUES ('1', '68');
INSERT INTO `sys_role_menu` VALUES ('1', '69');
INSERT INTO `sys_role_menu` VALUES ('1', '7');
INSERT INTO `sys_role_menu` VALUES ('1', '70');
INSERT INTO `sys_role_menu` VALUES ('1', '71');
INSERT INTO `sys_role_menu` VALUES ('1', '72');
INSERT INTO `sys_role_menu` VALUES ('1', '73');
INSERT INTO `sys_role_menu` VALUES ('1', '74');
INSERT INTO `sys_role_menu` VALUES ('1', '75');
INSERT INTO `sys_role_menu` VALUES ('1', '76');
INSERT INTO `sys_role_menu` VALUES ('1', '77');
INSERT INTO `sys_role_menu` VALUES ('1', '78');
INSERT INTO `sys_role_menu` VALUES ('1', '79');
INSERT INTO `sys_role_menu` VALUES ('1', '8');
INSERT INTO `sys_role_menu` VALUES ('1', '80');
INSERT INTO `sys_role_menu` VALUES ('1', '81');
INSERT INTO `sys_role_menu` VALUES ('1', '82');
INSERT INTO `sys_role_menu` VALUES ('1', '83');
INSERT INTO `sys_role_menu` VALUES ('1', '84');
INSERT INTO `sys_role_menu` VALUES ('1', '85');
INSERT INTO `sys_role_menu` VALUES ('1', '86');
INSERT INTO `sys_role_menu` VALUES ('1', '87');
INSERT INTO `sys_role_menu` VALUES ('1', '88');
INSERT INTO `sys_role_menu` VALUES ('1', '89');
INSERT INTO `sys_role_menu` VALUES ('1', '9');
INSERT INTO `sys_role_menu` VALUES ('1', '90');
INSERT INTO `sys_role_menu` VALUES ('2', '1');
INSERT INTO `sys_role_menu` VALUES ('2', '10');
INSERT INTO `sys_role_menu` VALUES ('2', '11');
INSERT INTO `sys_role_menu` VALUES ('2', '12');
INSERT INTO `sys_role_menu` VALUES ('2', '13');
INSERT INTO `sys_role_menu` VALUES ('2', '14');
INSERT INTO `sys_role_menu` VALUES ('2', '15');
INSERT INTO `sys_role_menu` VALUES ('2', '16');
INSERT INTO `sys_role_menu` VALUES ('2', '17');
INSERT INTO `sys_role_menu` VALUES ('2', '18');
INSERT INTO `sys_role_menu` VALUES ('2', '19');
INSERT INTO `sys_role_menu` VALUES ('2', '2');
INSERT INTO `sys_role_menu` VALUES ('2', '20');
INSERT INTO `sys_role_menu` VALUES ('2', '21');
INSERT INTO `sys_role_menu` VALUES ('2', '22');
INSERT INTO `sys_role_menu` VALUES ('2', '23');
INSERT INTO `sys_role_menu` VALUES ('2', '24');
INSERT INTO `sys_role_menu` VALUES ('2', '25');
INSERT INTO `sys_role_menu` VALUES ('2', '26');
INSERT INTO `sys_role_menu` VALUES ('2', '27');
INSERT INTO `sys_role_menu` VALUES ('2', '28');
INSERT INTO `sys_role_menu` VALUES ('2', '29');
INSERT INTO `sys_role_menu` VALUES ('2', '3');
INSERT INTO `sys_role_menu` VALUES ('2', '30');
INSERT INTO `sys_role_menu` VALUES ('2', '31');
INSERT INTO `sys_role_menu` VALUES ('2', '32');
INSERT INTO `sys_role_menu` VALUES ('2', '33');
INSERT INTO `sys_role_menu` VALUES ('2', '34');
INSERT INTO `sys_role_menu` VALUES ('2', '35');
INSERT INTO `sys_role_menu` VALUES ('2', '36');
INSERT INTO `sys_role_menu` VALUES ('2', '37');
INSERT INTO `sys_role_menu` VALUES ('2', '38');
INSERT INTO `sys_role_menu` VALUES ('2', '39');
INSERT INTO `sys_role_menu` VALUES ('2', '4');
INSERT INTO `sys_role_menu` VALUES ('2', '40');
INSERT INTO `sys_role_menu` VALUES ('2', '41');
INSERT INTO `sys_role_menu` VALUES ('2', '42');
INSERT INTO `sys_role_menu` VALUES ('2', '43');
INSERT INTO `sys_role_menu` VALUES ('2', '44');
INSERT INTO `sys_role_menu` VALUES ('2', '45');
INSERT INTO `sys_role_menu` VALUES ('2', '46');
INSERT INTO `sys_role_menu` VALUES ('2', '47');
INSERT INTO `sys_role_menu` VALUES ('2', '48');
INSERT INTO `sys_role_menu` VALUES ('2', '49');
INSERT INTO `sys_role_menu` VALUES ('2', '5');
INSERT INTO `sys_role_menu` VALUES ('2', '50');
INSERT INTO `sys_role_menu` VALUES ('2', '51');
INSERT INTO `sys_role_menu` VALUES ('2', '52');
INSERT INTO `sys_role_menu` VALUES ('2', '53');
INSERT INTO `sys_role_menu` VALUES ('2', '54');
INSERT INTO `sys_role_menu` VALUES ('2', '55');
INSERT INTO `sys_role_menu` VALUES ('2', '56');
INSERT INTO `sys_role_menu` VALUES ('2', '57');
INSERT INTO `sys_role_menu` VALUES ('2', '58');
INSERT INTO `sys_role_menu` VALUES ('2', '59');
INSERT INTO `sys_role_menu` VALUES ('2', '6');
INSERT INTO `sys_role_menu` VALUES ('2', '60');
INSERT INTO `sys_role_menu` VALUES ('2', '61');
INSERT INTO `sys_role_menu` VALUES ('2', '62');
INSERT INTO `sys_role_menu` VALUES ('2', '63');
INSERT INTO `sys_role_menu` VALUES ('2', '64');
INSERT INTO `sys_role_menu` VALUES ('2', '65');
INSERT INTO `sys_role_menu` VALUES ('2', '66');
INSERT INTO `sys_role_menu` VALUES ('2', '67');
INSERT INTO `sys_role_menu` VALUES ('2', '68');
INSERT INTO `sys_role_menu` VALUES ('2', '69');
INSERT INTO `sys_role_menu` VALUES ('2', '7');
INSERT INTO `sys_role_menu` VALUES ('2', '70');
INSERT INTO `sys_role_menu` VALUES ('2', '71');
INSERT INTO `sys_role_menu` VALUES ('2', '72');
INSERT INTO `sys_role_menu` VALUES ('2', '73');
INSERT INTO `sys_role_menu` VALUES ('2', '74');
INSERT INTO `sys_role_menu` VALUES ('2', '75');
INSERT INTO `sys_role_menu` VALUES ('2', '76');
INSERT INTO `sys_role_menu` VALUES ('2', '77');
INSERT INTO `sys_role_menu` VALUES ('2', '78');
INSERT INTO `sys_role_menu` VALUES ('2', '79');
INSERT INTO `sys_role_menu` VALUES ('2', '8');
INSERT INTO `sys_role_menu` VALUES ('2', '80');
INSERT INTO `sys_role_menu` VALUES ('2', '81');
INSERT INTO `sys_role_menu` VALUES ('2', '82');
INSERT INTO `sys_role_menu` VALUES ('2', '83');
INSERT INTO `sys_role_menu` VALUES ('2', '84');
INSERT INTO `sys_role_menu` VALUES ('2', '85');
INSERT INTO `sys_role_menu` VALUES ('2', '86');
INSERT INTO `sys_role_menu` VALUES ('2', '87');
INSERT INTO `sys_role_menu` VALUES ('2', '88');
INSERT INTO `sys_role_menu` VALUES ('2', '89');
INSERT INTO `sys_role_menu` VALUES ('2', '9');
INSERT INTO `sys_role_menu` VALUES ('2', '90');
INSERT INTO `sys_role_menu` VALUES ('3', '1');
INSERT INTO `sys_role_menu` VALUES ('3', '10');
INSERT INTO `sys_role_menu` VALUES ('3', '11');
INSERT INTO `sys_role_menu` VALUES ('3', '12');
INSERT INTO `sys_role_menu` VALUES ('3', '13');
INSERT INTO `sys_role_menu` VALUES ('3', '14');
INSERT INTO `sys_role_menu` VALUES ('3', '15');
INSERT INTO `sys_role_menu` VALUES ('3', '16');
INSERT INTO `sys_role_menu` VALUES ('3', '17');
INSERT INTO `sys_role_menu` VALUES ('3', '18');
INSERT INTO `sys_role_menu` VALUES ('3', '19');
INSERT INTO `sys_role_menu` VALUES ('3', '2');
INSERT INTO `sys_role_menu` VALUES ('3', '20');
INSERT INTO `sys_role_menu` VALUES ('3', '21');
INSERT INTO `sys_role_menu` VALUES ('3', '22');
INSERT INTO `sys_role_menu` VALUES ('3', '23');
INSERT INTO `sys_role_menu` VALUES ('3', '24');
INSERT INTO `sys_role_menu` VALUES ('3', '25');
INSERT INTO `sys_role_menu` VALUES ('3', '26');
INSERT INTO `sys_role_menu` VALUES ('3', '27');
INSERT INTO `sys_role_menu` VALUES ('3', '28');
INSERT INTO `sys_role_menu` VALUES ('3', '29');
INSERT INTO `sys_role_menu` VALUES ('3', '3');
INSERT INTO `sys_role_menu` VALUES ('3', '30');
INSERT INTO `sys_role_menu` VALUES ('3', '31');
INSERT INTO `sys_role_menu` VALUES ('3', '32');
INSERT INTO `sys_role_menu` VALUES ('3', '33');
INSERT INTO `sys_role_menu` VALUES ('3', '34');
INSERT INTO `sys_role_menu` VALUES ('3', '35');
INSERT INTO `sys_role_menu` VALUES ('3', '36');
INSERT INTO `sys_role_menu` VALUES ('3', '37');
INSERT INTO `sys_role_menu` VALUES ('3', '38');
INSERT INTO `sys_role_menu` VALUES ('3', '39');
INSERT INTO `sys_role_menu` VALUES ('3', '4');
INSERT INTO `sys_role_menu` VALUES ('3', '40');
INSERT INTO `sys_role_menu` VALUES ('3', '41');
INSERT INTO `sys_role_menu` VALUES ('3', '42');
INSERT INTO `sys_role_menu` VALUES ('3', '43');
INSERT INTO `sys_role_menu` VALUES ('3', '44');
INSERT INTO `sys_role_menu` VALUES ('3', '45');
INSERT INTO `sys_role_menu` VALUES ('3', '46');
INSERT INTO `sys_role_menu` VALUES ('3', '47');
INSERT INTO `sys_role_menu` VALUES ('3', '48');
INSERT INTO `sys_role_menu` VALUES ('3', '49');
INSERT INTO `sys_role_menu` VALUES ('3', '5');
INSERT INTO `sys_role_menu` VALUES ('3', '50');
INSERT INTO `sys_role_menu` VALUES ('3', '51');
INSERT INTO `sys_role_menu` VALUES ('3', '52');
INSERT INTO `sys_role_menu` VALUES ('3', '53');
INSERT INTO `sys_role_menu` VALUES ('3', '54');
INSERT INTO `sys_role_menu` VALUES ('3', '55');
INSERT INTO `sys_role_menu` VALUES ('3', '56');
INSERT INTO `sys_role_menu` VALUES ('3', '57');
INSERT INTO `sys_role_menu` VALUES ('3', '58');
INSERT INTO `sys_role_menu` VALUES ('3', '59');
INSERT INTO `sys_role_menu` VALUES ('3', '6');
INSERT INTO `sys_role_menu` VALUES ('3', '60');
INSERT INTO `sys_role_menu` VALUES ('3', '61');
INSERT INTO `sys_role_menu` VALUES ('3', '62');
INSERT INTO `sys_role_menu` VALUES ('3', '63');
INSERT INTO `sys_role_menu` VALUES ('3', '64');
INSERT INTO `sys_role_menu` VALUES ('3', '65');
INSERT INTO `sys_role_menu` VALUES ('3', '66');
INSERT INTO `sys_role_menu` VALUES ('3', '67');
INSERT INTO `sys_role_menu` VALUES ('3', '68');
INSERT INTO `sys_role_menu` VALUES ('3', '69');
INSERT INTO `sys_role_menu` VALUES ('3', '7');
INSERT INTO `sys_role_menu` VALUES ('3', '70');
INSERT INTO `sys_role_menu` VALUES ('3', '71');
INSERT INTO `sys_role_menu` VALUES ('3', '72');
INSERT INTO `sys_role_menu` VALUES ('3', '73');
INSERT INTO `sys_role_menu` VALUES ('3', '74');
INSERT INTO `sys_role_menu` VALUES ('3', '75');
INSERT INTO `sys_role_menu` VALUES ('3', '76');
INSERT INTO `sys_role_menu` VALUES ('3', '77');
INSERT INTO `sys_role_menu` VALUES ('3', '78');
INSERT INTO `sys_role_menu` VALUES ('3', '79');
INSERT INTO `sys_role_menu` VALUES ('3', '8');
INSERT INTO `sys_role_menu` VALUES ('3', '80');
INSERT INTO `sys_role_menu` VALUES ('3', '81');
INSERT INTO `sys_role_menu` VALUES ('3', '82');
INSERT INTO `sys_role_menu` VALUES ('3', '83');
INSERT INTO `sys_role_menu` VALUES ('3', '84');
INSERT INTO `sys_role_menu` VALUES ('3', '85');
INSERT INTO `sys_role_menu` VALUES ('3', '86');
INSERT INTO `sys_role_menu` VALUES ('3', '87');
INSERT INTO `sys_role_menu` VALUES ('3', '88');
INSERT INTO `sys_role_menu` VALUES ('3', '89');
INSERT INTO `sys_role_menu` VALUES ('3', '9');
INSERT INTO `sys_role_menu` VALUES ('3', '90');

-- ----------------------------
-- Table structure for sys_role_office
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_office`;
CREATE TABLE `sys_role_office` (
  `role_id` varchar(64) NOT NULL COMMENT '角色编号',
  `office_id` varchar(64) NOT NULL COMMENT '机构编号',
  PRIMARY KEY (`role_id`,`office_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色-机构';

-- ----------------------------
-- Records of sys_role_office
-- ----------------------------
INSERT INTO `sys_role_office` VALUES ('7', '10');
INSERT INTO `sys_role_office` VALUES ('7', '11');
INSERT INTO `sys_role_office` VALUES ('7', '12');
INSERT INTO `sys_role_office` VALUES ('7', '13');
INSERT INTO `sys_role_office` VALUES ('7', '14');
INSERT INTO `sys_role_office` VALUES ('7', '15');
INSERT INTO `sys_role_office` VALUES ('7', '16');
INSERT INTO `sys_role_office` VALUES ('7', '17');
INSERT INTO `sys_role_office` VALUES ('7', '18');
INSERT INTO `sys_role_office` VALUES ('7', '19');
INSERT INTO `sys_role_office` VALUES ('7', '20');
INSERT INTO `sys_role_office` VALUES ('7', '21');
INSERT INTO `sys_role_office` VALUES ('7', '22');
INSERT INTO `sys_role_office` VALUES ('7', '23');
INSERT INTO `sys_role_office` VALUES ('7', '24');
INSERT INTO `sys_role_office` VALUES ('7', '25');
INSERT INTO `sys_role_office` VALUES ('7', '26');
INSERT INTO `sys_role_office` VALUES ('7', '7');
INSERT INTO `sys_role_office` VALUES ('7', '8');
INSERT INTO `sys_role_office` VALUES ('7', '9');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` varchar(64) NOT NULL COMMENT '编号',
  `company_id` varchar(64) NOT NULL COMMENT '归属公司',
  `office_id` varchar(64) NOT NULL COMMENT '归属部门',
  `login_name` varchar(100) NOT NULL COMMENT '登录名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `no` varchar(100) DEFAULT NULL COMMENT '工号',
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `email` varchar(200) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(200) DEFAULT NULL COMMENT '电话',
  `mobile` varchar(200) DEFAULT NULL COMMENT '手机',
  `user_type` char(1) DEFAULT NULL COMMENT '用户类型',
  `photo` varchar(1000) DEFAULT NULL COMMENT '用户头像',
  `login_ip` varchar(100) DEFAULT NULL COMMENT '最后登陆IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登陆时间',
  `login_flag` varchar(64) DEFAULT NULL COMMENT '是否可登录',
  `create_by` varchar(64) NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `sys_user_office_id` (`office_id`),
  KEY `sys_user_login_name` (`login_name`),
  KEY `sys_user_company_id` (`company_id`),
  KEY `sys_user_update_date` (`update_date`),
  KEY `sys_user_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', '54d05cbfef4f4bafb39ec67d0d26211c', '56c6fb3511a548c09fb0964b00b2145c', 'admin', '02a3f0772fcca9f415adc990734b45c6f059c7d33ee28362c4852032', '0001', '系统管理员', '', '8675', '8675', '', '/iLife/userfiles/1/images/photo/2016/11/4c69eb5b0d4761b6da06a22e3d950f06.jpg', '0:0:0:0:0:0:0:1', '2016-11-03 17:28:11', '1', '1', '2013-05-27 00:00:00', '1', '2016-11-03 16:32:48', '最高管理员', '0');
INSERT INTO `sys_user` VALUES ('295def53ef614f06b164f78c4944eb4d', '54d05cbfef4f4bafb39ec67d0d26211c', '56c6fb3511a548c09fb0964b00b2145c', 'shenheren1', 'ea3e4e158efc509925c3d95791a2038e7240d5daeae7bd5a89e7c0ff', '001', '审核人1', '', '', '', '1', '/iLife/userfiles/1/images/photo/2016/11/4c69eb5b0d4761b6da06a22e3d950f06.jpg', '0:0:0:0:0:0:0:1', '2016-11-04 12:50:03', '1', '1', '2016-11-03 16:35:26', '1', '2016-11-03 16:35:26', '', '0');
INSERT INTO `sys_user` VALUES ('7833ae14c3cf4440a98a3301770e2df5', '54d05cbfef4f4bafb39ec67d0d26211c', '56c6fb3511a548c09fb0964b00b2145c', 'shenheren2', 'abd6d095e2b1a30643932b2f1c685cc3d10f26e26c5be13c51102790', '002', '审核人2', '', '', '', '1', '/iLife/userfiles/1/images/photo/2016/11/4c69eb5b0d4761b6da06a22e3d950f06.jpg', null, null, '1', '1', '2016-11-03 16:36:04', '1', '2016-11-03 16:36:04', '', '0');
INSERT INTO `sys_user` VALUES ('ab107f6aaff2421db611f6f3d65f6fc6', '54d05cbfef4f4bafb39ec67d0d26211c', '56c6fb3511a548c09fb0964b00b2145c', 'chenci', '68e47d3ec2af18f80dfa742751afcaf30fc7f7d6f1ec77e85923004e', '003', 'chenci', '', '', '', '1', '/iLife/userfiles/1/images/photo/2016/11/4c69eb5b0d4761b6da06a22e3d950f06.jpg', null, null, '1', '1', '2016-11-03 16:36:49', '1', '2016-11-03 16:36:49', '', '0');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` varchar(64) NOT NULL COMMENT '用户编号',
  `role_id` varchar(64) NOT NULL COMMENT '角色编号',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户-角色';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1', '1');
INSERT INTO `sys_user_role` VALUES ('1', '2');
INSERT INTO `sys_user_role` VALUES ('10', '2');
INSERT INTO `sys_user_role` VALUES ('11', '3');
INSERT INTO `sys_user_role` VALUES ('12', '4');
INSERT INTO `sys_user_role` VALUES ('13', '5');
INSERT INTO `sys_user_role` VALUES ('14', '6');
INSERT INTO `sys_user_role` VALUES ('2', '1');
INSERT INTO `sys_user_role` VALUES ('295def53ef614f06b164f78c4944eb4d', '1');
INSERT INTO `sys_user_role` VALUES ('295def53ef614f06b164f78c4944eb4d', '2');
INSERT INTO `sys_user_role` VALUES ('295def53ef614f06b164f78c4944eb4d', '3');
INSERT INTO `sys_user_role` VALUES ('295def53ef614f06b164f78c4944eb4d', '4');
INSERT INTO `sys_user_role` VALUES ('295def53ef614f06b164f78c4944eb4d', '5');
INSERT INTO `sys_user_role` VALUES ('295def53ef614f06b164f78c4944eb4d', '6');
INSERT INTO `sys_user_role` VALUES ('295def53ef614f06b164f78c4944eb4d', '7');
INSERT INTO `sys_user_role` VALUES ('3', '2');
INSERT INTO `sys_user_role` VALUES ('4', '3');
INSERT INTO `sys_user_role` VALUES ('5', '4');
INSERT INTO `sys_user_role` VALUES ('6', '5');
INSERT INTO `sys_user_role` VALUES ('7', '2');
INSERT INTO `sys_user_role` VALUES ('7', '7');
INSERT INTO `sys_user_role` VALUES ('7833ae14c3cf4440a98a3301770e2df5', '1');
INSERT INTO `sys_user_role` VALUES ('7833ae14c3cf4440a98a3301770e2df5', '2');
INSERT INTO `sys_user_role` VALUES ('7833ae14c3cf4440a98a3301770e2df5', '3');
INSERT INTO `sys_user_role` VALUES ('7833ae14c3cf4440a98a3301770e2df5', '4');
INSERT INTO `sys_user_role` VALUES ('7833ae14c3cf4440a98a3301770e2df5', '5');
INSERT INTO `sys_user_role` VALUES ('7833ae14c3cf4440a98a3301770e2df5', '6');
INSERT INTO `sys_user_role` VALUES ('7833ae14c3cf4440a98a3301770e2df5', '7');
INSERT INTO `sys_user_role` VALUES ('8', '2');
INSERT INTO `sys_user_role` VALUES ('9', '1');
INSERT INTO `sys_user_role` VALUES ('ab107f6aaff2421db611f6f3d65f6fc6', '1');
INSERT INTO `sys_user_role` VALUES ('ab107f6aaff2421db611f6f3d65f6fc6', '2');
INSERT INTO `sys_user_role` VALUES ('ab107f6aaff2421db611f6f3d65f6fc6', '3');
INSERT INTO `sys_user_role` VALUES ('ab107f6aaff2421db611f6f3d65f6fc6', '4');
INSERT INTO `sys_user_role` VALUES ('ab107f6aaff2421db611f6f3d65f6fc6', '5');
INSERT INTO `sys_user_role` VALUES ('ab107f6aaff2421db611f6f3d65f6fc6', '6');
INSERT INTO `sys_user_role` VALUES ('ab107f6aaff2421db611f6f3d65f6fc6', '7');
