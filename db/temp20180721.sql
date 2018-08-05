alter table mod_tag add column rule_of_judgment varchar(500);
alter table mod_tag add column tag_key varchar(128);

alter table mod_hierarchy change  column society_score society_score_min DOUBLE;
alter table mod_hierarchy change  column culture_score culture_score_min DOUBLE;
alter table mod_hierarchy change  column economy_score economy_score_min DOUBLE;
alter table mod_hierarchy add column culture_score_max DOUBLE;
alter table mod_hierarchy add column economy_score_max DOUBLE;
alter table mod_hierarchy add column society_score_max DOUBLE;

alter table mod_occasion_category add column trigger_direction VARCHAR(10);
alter table mod_occasion_category add column trigger_type VARCHAR(10);

alter table mod_capability add column capability_name VARCHAR(20);

alter table ope_person add column hierarchy_id VARCHAR(64);

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for mod_life_style_category
-- ----------------------------
DROP TABLE IF EXISTS `mod_life_style_category`;
CREATE TABLE `mod_life_style_category` (
  `id` varchar(64) DEFAULT NULL,
  `parent_id` varchar(64) NOT NULL COMMENT '父级编号',
  `parent_ids` varchar(2000) NOT NULL COMMENT '所有父级编号',
  `name` varchar(100) DEFAULT NULL,
  `logo` varchar(2000) DEFAULT NULL,
  `description` text,
  `sort` int(11) DEFAULT '30' COMMENT '排序（升序）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of mod_life_style_category
-- ----------------------------
INSERT INTO `mod_life_style_category` VALUES ('1', '0', '0,', '顶级分类', null, null, '30', '1', '2018-08-05 17:06:15', '1', '2018-08-05 17:06:18', '0');

alter table mod_life_style add column category_id VARCHAR(64);