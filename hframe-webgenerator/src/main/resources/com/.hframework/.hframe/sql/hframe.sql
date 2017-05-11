CREATE TABLE `cntsms` (
  `sms_id` int(11) NOT NULL AUTO_INCREMENT,
  `from_system` varchar(20) DEFAULT NULL,
  `from_id` varchar(20) DEFAULT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `content` varchar(512) DEFAULT NULL,
  `send_time` datetime DEFAULT NULL,
  `send_flag` char(1) DEFAULT NULL,
  `report_stats` char(1) DEFAULT NULL,
  `excode` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`sms_id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=gbk;


CREATE TABLE `comment` (
  `comment_id` varchar(64) NOT NULL,
  `diary_id` varchar(64) DEFAULT NULL,
  `parent_coment_id` varchar(65) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `content` longtext,
  `user_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_action` (
  `id` varchar(64) NOT NULL,
  `action_name` varchar(128) DEFAULT NULL,
  `action_method` varchar(64) DEFAULT NULL,
  `table_id` varchar(64) DEFAULT NULL,
  `columns_id` varchar(64) DEFAULT NULL,
  `description` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_column_enum_rel` (
  `core_column_enum_rel_id` varchar(32) NOT NULL,
  `core_column_id` varchar(32) NOT NULL,
  `core_enum_group_id` varchar(32) DEFAULT NULL,
  `column_enum_dyn_id` varchar(32) DEFAULT NULL,
  `show_type` int(2) DEFAULT NULL,
  `ext1` varchar(32) DEFAULT NULL,
  `ext2` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`core_column_enum_rel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_component` (
  `id` varchar(64) NOT NULL,
  `component_name` varchar(128) DEFAULT NULL,
  `content` text,
  `sort` varchar(64) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_control` (
  `id` varchar(64) NOT NULL,
  `control_name` varchar(64) DEFAULT NULL,
  `control_value` varchar(256) DEFAULT NULL,
  `control_quote` varchar(256) DEFAULT NULL,
  `attr` varchar(64) DEFAULT NULL,
  `descption` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_db` (
  `id` varchar(64) NOT NULL,
  `db_name` varchar(64) DEFAULT NULL,
  `show_name` varchar(256) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `descprition` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_element` (
  `core_element_id` varchar(32) NOT NULL,
  `core_element_type` int(3) DEFAULT NULL,
  `content` varchar(512) NOT NULL,
  `condition` varchar(128) DEFAULT NULL,
  `objects` varchar(128) DEFAULT NULL,
  `desc` varchar(128) DEFAULT NULL,
  `ext1` varchar(128) DEFAULT NULL,
  `ext2` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`core_element_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_enum` (
  `core_enum_id` varchar(32) NOT NULL,
  `core_enum_group_id` varchar(32) NOT NULL,
  `core_enum_group_show_type` int(2) DEFAULT NULL,
  `core_enum_value` varchar(32) NOT NULL,
  `core_enum_display_value` varchar(32) DEFAULT NULL,
  `core_enum_default` int(2) DEFAULT NULL,
  `core_enum_pri` int(11) DEFAULT NULL,
  `core_enum_desc` varchar(128) DEFAULT NULL,
  `ext1` varchar(32) DEFAULT NULL,
  `ext2` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`core_enum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_enum_bak` (
  `core_enum_id` varchar(32) NOT NULL,
  `core_enum_group_id` varchar(32) NOT NULL,
  `core_enum_group_show_type` int(2) DEFAULT NULL,
  `core_enum_value` varchar(32) NOT NULL,
  `core_enum_display_value` varchar(32) DEFAULT NULL,
  `core_enum_default` int(2) DEFAULT NULL,
  `core_enum_pri` int(11) DEFAULT NULL,
  `core_enum_desc` varchar(128) DEFAULT NULL,
  `ext1` varchar(32) DEFAULT NULL,
  `ext2` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_enum_dyn` (
  `core_enum_id` varchar(32) NOT NULL,
  `core_enum_group_id` varchar(32) DEFAULT NULL,
  `show_type` int(2) DEFAULT NULL,
  `sql` varchar(256) NOT NULL,
  `condition` varchar(128) DEFAULT NULL,
  `order_condition` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`core_enum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_enum_group` (
  `core_enum_group_id` varchar(32) NOT NULL,
  `core_enmu_group_show_type` varchar(30) DEFAULT NULL,
  `core_enum_group_name` varchar(32) DEFAULT NULL,
  `core_enum_desc` varchar(128) DEFAULT NULL,
  `ext1` varchar(128) DEFAULT NULL,
  `ext2` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`core_enum_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_enum_group_bak` (
  `core_enum_group_id` varchar(32) NOT NULL,
  `core_enmu_group_show_type` varchar(30) DEFAULT NULL,
  `core_enum_group_name` varchar(32) DEFAULT NULL,
  `core_enum_desc` varchar(128) DEFAULT NULL,
  `ext1` varchar(128) DEFAULT NULL,
  `ext2` varchar(128) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_field` (
  `id` varchar(32) NOT NULL,
  `core_set_id` varchar(32) DEFAULT NULL,
  `type` varchar(32) DEFAULT NULL,
  `title` varchar(128) DEFAULT NULL,
  `show_exp` varchar(256) DEFAULT NULL,
  `hidden_exp` varchar(256) DEFAULT NULL,
  `ext1` varchar(32) DEFAULT NULL,
  `ext2` varchar(128) DEFAULT NULL,
  `priv` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_page` (
  `id` varchar(64) NOT NULL,
  `page_name` varchar(64) NOT NULL,
  `method_id` varchar(64) DEFAULT NULL,
  `page_path` varchar(256) DEFAULT NULL,
  `page_content` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_set` (
  `core_set_id` varchar(32) NOT NULL,
  `core_set_name` varchar(128) NOT NULL,
  `core_set_type` varchar(32) DEFAULT NULL,
  `core_set_tables` varchar(256) DEFAULT NULL,
  `core_set_quote` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`core_set_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_set_column_rel` (
  `core_set_column_rel_id` varchar(32) NOT NULL,
  `core_set_id` varchar(32) NOT NULL,
  `core_column_id` varchar(32) NOT NULL,
  `ext1` varchar(128) DEFAULT NULL,
  `ext2` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`core_set_column_rel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_show_type` (
  `id` varchar(32) NOT NULL,
  `type` varchar(32) DEFAULT NULL,
  `col_span` int(2) DEFAULT NULL,
  `row_span` int(2) DEFAULT NULL,
  `value` varchar(64) DEFAULT NULL,
  `text` varchar(256) DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `pre_str` varchar(256) DEFAULT NULL,
  `after_str` varchar(256) DEFAULT NULL,
  `core_enum_group_id` varchar(32) DEFAULT NULL,
  `core_element_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_show_type_attr` (
  `id` varchar(64) NOT NULL,
  `view` varchar(128) DEFAULT NULL,
  `type` varchar(32) DEFAULT NULL,
  `condition` varchar(256) DEFAULT NULL,
  `title` varchar(256) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `src` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_table` (
  `table_id` varchar(64) NOT NULL,
  `table_name` varchar(64) NOT NULL,
  `table_desc` varchar(124) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `db_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `core_table_column` (
  `id` varchar(64) NOT NULL,
  `column_name` varchar(64) NOT NULL,
  `column_type` varchar(64) NOT NULL,
  `show_type_id` varchar(32) DEFAULT NULL,
  `column_size` int(11) DEFAULT NULL,
  `ispk` int(11) DEFAULT NULL,
  `nullable` int(11) DEFAULT NULL,
  `showable` int(11) DEFAULT NULL,
  `show_name` varchar(64) DEFAULT NULL,
  `table_id` varchar(64) NOT NULL,
  `description` varchar(128) DEFAULT NULL,
  `priv` decimal(2,0) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


core_table_db;


CREATE TABLE `core_table_fk` (
  `id` varchar(64) NOT NULL,
  `column_id_from` varchar(64) NOT NULL,
  `column_id_to` varchar(64) NOT NULL,
  `type` int(11) DEFAULT NULL,
  `flag` int(11) DEFAULT NULL,
  `description` varchar(128) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `diary` (
  `diary_id` varchar(64) NOT NULL,
  `title` longtext,
  `create_date` datetime DEFAULT NULL,
  `content` longtext,
  `diary_group_id` varchar(64) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  `diary_level` int(11) DEFAULT NULL,
  PRIMARY KEY (`diary_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `diary_group` (
  `diary_group_id` varchar(64) NOT NULL,
  `create_date` datetime DEFAULT NULL,
  `creator_id` varchar(64) DEFAULT NULL,
  `diary_group_name` longtext,
  PRIMARY KEY (`diary_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `full_form_test` (
  `id` varchar(64) NOT NULL,
  `name` varchar(128) DEFAULT NULL,
  `gender` int(11) DEFAULT NULL,
  `org_id` varchar(64) DEFAULT NULL,
  `valid_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `hfcfg_login_page` (
  `hfcfg_login_page_id` bigint(20) unsigned zerofill NOT NULL COMMENT '登陆页面ID',
  `hfcfg_login_page_name` varchar(64) DEFAULT NULL COMMENT '登陆页面名称',
  `hfcfg_login_page_code` varchar(64) DEFAULT NULL COMMENT '登陆页面编码',
  `snapshot_url` varchar(128) DEFAULT NULL COMMENT '页面快照URL',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfcfg_login_page_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='登陆页面';


CREATE TABLE `hfcfg_page_template` (
  `hfcfg_page_template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '页面模板ID',
  `hfcfg_page_template_type` int(2) NOT NULL COMMENT '页面模板类型',
  `hfcfg_page_template_name` varchar(64) DEFAULT NULL COMMENT '页面模板名称',
  `hfcfg_page_template_code` varchar(128) NOT NULL COMMENT '页面模板编码',
  `hfcfg_page_template_desc` varchar(128) DEFAULT NULL COMMENT '页面模板描述',
  `template_url` varchar(128) DEFAULT NULL COMMENT '模板地址',
  `snapshot_url` varchar(128) DEFAULT NULL COMMENT '快照URL',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfcfg_page_template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='页面模板';


CREATE TABLE `hfcfg_program_skin` (
  `hfcfg_program_skin_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目皮肤ID',
  `program_skin_name` varchar(64) DEFAULT NULL COMMENT '项目皮肤名称',
  `program_skin_code` varchar(64) DEFAULT NULL COMMENT '项目皮肤编码',
  `snapshot_url` varchar(128) DEFAULT NULL COMMENT '快照URL',
  `program_template_id` bigint(20) DEFAULT NULL COMMENT '项目模板ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfcfg_program_skin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目皮肤';


CREATE TABLE `hfcfg_program_template` (
  `hfcfg_program_template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目模板ID',
  `program_template_name` varchar(64) DEFAULT NULL COMMENT '项目模板名称',
  `program_template_code` varchar(64) DEFAULT NULL COMMENT '项目模板编码',
  `program_template_desc` varchar(128) DEFAULT NULL COMMENT '项目模板描述',
  `template_url` varchar(128) DEFAULT NULL COMMENT '模板地址',
  `snapshot_url` varchar(128) DEFAULT NULL COMMENT '快照URL',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfcfg_program_template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='项目模板';


CREATE TABLE `hfmd_entity` (
  `hfmd_entity_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '实体ID',
  `hfmd_entity_name` varchar(64) NOT NULL COMMENT '实体名称',
  `hfmd_entity_code` varchar(64) NOT NULL COMMENT '实体编码',
  `hfmd_entity_type` int(2) NOT NULL COMMENT '实体类型',
  `hfmd_entity_desc` varchar(124) DEFAULT NULL COMMENT '实体描述',
  `hfpm_program_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `hfpm_module_id` bigint(20) DEFAULT NULL COMMENT '模块ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfmd_entity_id`),
  KEY `FKDB525C4DF6C2F764` (`hfpm_module_id`),
  KEY `FKDB525C4DBBCC2B50` (`hfpm_program_id`)
) ENGINE=InnoDB AUTO_INCREMENT=151031866026 DEFAULT CHARSET=utf8 COMMENT='实体';


CREATE TABLE `hfmd_entity_attr` (
  `hfmd_entity_attr_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '实体属性ID',
  `hfmd_entity_attr_name` varchar(64) NOT NULL COMMENT '实体属性名称',
  `hfmd_entity_attr_code` varchar(64) NOT NULL COMMENT '实体属性编码',
  `hfmd_entity_attr_desc` varchar(128) DEFAULT NULL COMMENT '实体属性描述',
  `attr_type` int(2) NOT NULL COMMENT '属性类型',
  `size` varchar(6) DEFAULT NULL COMMENT '大小',
  `ispk` int(2) DEFAULT NULL COMMENT '是否主键',
  `nullable` int(2) DEFAULT NULL COMMENT '是否为空',
  `is_busi_attr` int(2) DEFAULT NULL COMMENT '是否业务属性',
  `is_redundant_attr` int(2) DEFAULT NULL COMMENT '是否冗余属性',
  `rel_hfmd_entity_attr_id` bigint(20) DEFAULT NULL COMMENT '关联属性ID',
  `hfmd_enum_class_id` bigint(20) DEFAULT NULL COMMENT '枚举类ID',
  `pri` decimal(6,2) DEFAULT NULL COMMENT '优先级',
  `hfpm_program_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `hfpm_module_id` bigint(20) DEFAULT NULL COMMENT '模块ID',
  `hfmd_entity_id` bigint(20) DEFAULT NULL COMMENT '实体ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfmd_entity_attr_id`),
  KEY `FKEE94DEA39747FE05` (`hfmd_enum_class_id`),
  KEY `FKEE94DEA3F6C2F764` (`hfpm_module_id`),
  KEY `FKEE94DEA3B2991B38` (`hfmd_entity_id`),
  KEY `FKEE94DEA3BBCC2B50` (`hfpm_program_id`)
) ENGINE=InnoDB AUTO_INCREMENT=151031888609 DEFAULT CHARSET=utf8 COMMENT='实体属性';


CREATE TABLE `hfmd_entity_join_rule` (
  `hfmd_entity_join_rule_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '实体连带id',
  `source_hfmd_entity_id` bigint(20) NOT NULL COMMENT '源实体id',
  `source_hfmd_entity_attr_id` bigint(20) NOT NULL COMMENT '源实体属性id',
  `source_hfmd_entity_attr_value` varchar(64) DEFAULT NULL COMMENT '源实体属性值',
  `join_type` tinyint(2) NOT NULL COMMENT '连带类型 1-值对应 2-值关联',
  `target_hfmd_entity_id` bigint(20) NOT NULL COMMENT '目标实体id',
  `target_hfmd_entity_attr_id` bigint(20) NOT NULL COMMENT '目标实体属性id',
  `target_hfmd_entity_attr_value` varchar(64) DEFAULT NULL COMMENT '目标实体属性值',
  `editable` tinyint(2) DEFAULT NULL COMMENT '是否可编辑 0-否 1-是',
  PRIMARY KEY (`hfmd_entity_join_rule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='实体连带关系';


CREATE TABLE `hfmd_entity_rel` (
  `hfmd_entity_rel_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '实体关系ID',
  `hfmd_entity_id` bigint(20) DEFAULT NULL COMMENT '实体ID',
  `hfmd_entity_rel_type` int(2) DEFAULT NULL COMMENT '实体关联类型\n            0：一对一\n            1：一对多\n            2：多对一\n            3：多对多',
  `hfmd_entity_rel_level` int(2) DEFAULT NULL COMMENT '实体关联级别\n            0 ：弱关联（引用）\n            1 ：强关联（归属）',
  `hfmd_entity_rel_desc` varchar(128) DEFAULT NULL COMMENT '实体关联描述',
  `rel_hfmd_entity_id` bigint(20) DEFAULT NULL COMMENT '关联实体ID',
  `rel_entity_attr_id` bigint(20) DEFAULT NULL,
  `hfpm_program_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfmd_entity_rel_id`),
  KEY `FKF72E55A7B2991B38` (`hfmd_entity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='实体关系';


CREATE TABLE `hfmd_enum` (
  `hfmd_enum_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '枚举ID',
  `hfmd_enum_value` varchar(32) NOT NULL COMMENT '枚举值',
  `hfmd_enum_text` varchar(32) NOT NULL COMMENT '枚举文本',
  `hfmd_enum_desc` varchar(128) DEFAULT NULL COMMENT '枚举描述',
  `is_default` int(2) DEFAULT NULL COMMENT '是否默认',
  `pri` decimal(4,2) DEFAULT NULL COMMENT '优先级',
  `ext1` varchar(128) DEFAULT NULL COMMENT '扩展字段1',
  `ext2` varchar(128) DEFAULT NULL COMMENT '扩展字段2',
  `hfmd_enum_class_id` bigint(20) NOT NULL COMMENT '枚举类目ID',
  `hfmd_enum_class_code` varchar(32) NOT NULL,
  `hfpm_program_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfmd_enum_id`),
  KEY `FKD59B1FEBBBCC2B50` (`hfpm_program_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COMMENT='枚举';


CREATE TABLE `hfmd_enum_class` (
  `hfmd_enum_class_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '枚举类目ID',
  `hfmd_enum_class_name` varchar(32) NOT NULL COMMENT '枚举类目名称',
  `hfmd_enum_class_code` varchar(32) NOT NULL COMMENT '枚举类目编码',
  `hfmd_enum_class_desc` varchar(128) DEFAULT NULL COMMENT '枚举类目描述',
  `ext1` varchar(128) DEFAULT NULL COMMENT '扩展字段1',
  `ext2` varchar(128) DEFAULT NULL COMMENT '扩展字段2',
  `hfpm_program_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfmd_enum_class_id`),
  KEY `FKDA25024BBCC2B50` (`hfpm_program_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COMMENT='枚举类目';


CREATE TABLE `hfpm_data_field` (
  `hfpm_data_field_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据列ID',
  `hfpm_data_field_code` varchar(64) DEFAULT NULL COMMENT '数据列编码',
  `hfpm_field_show_type_id` varchar(32) DEFAULT NULL COMMENT '列展示类型ID',
  `field_show_code` varchar(6) DEFAULT NULL COMMENT '列展示码',
  `hfmd_entity_id` bigint(20) DEFAULT NULL COMMENT '实体ID',
  `hfmd_entity_attr_id` bigint(20) DEFAULT NULL COMMENT '实体属性ID',
  `data_get_method` int(2) DEFAULT NULL COMMENT '数据获取方式',
  `hfpm_data_field_name` varchar(64) DEFAULT NULL COMMENT '数据列名称',
  `hfpm_data_set_id` bigint(20) DEFAULT NULL COMMENT '数据集ID',
  `pri` decimal(6,2) DEFAULT NULL COMMENT '优先级',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_data_field_id`)
) ENGINE=InnoDB AUTO_INCREMENT=151031888816 DEFAULT CHARSET=utf8 COMMENT='数据列';


CREATE TABLE `hfpm_data_set` (
  `hfpm_data_set_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据集ID',
  `hfpm_data_set_name` varchar(64) DEFAULT NULL COMMENT '数据集名称',
  `hfpm_data_set_code` varchar(64) DEFAULT NULL COMMENT '数据集编码',
  `main_hfmd_entity_id` bigint(20) DEFAULT NULL COMMENT '主实体ID',
  `hfpm_program_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_data_set_id`)
) ENGINE=InnoDB AUTO_INCREMENT=151031887767 DEFAULT CHARSET=utf8 COMMENT='数据集';


CREATE TABLE `hfpm_entity_bind_rule` (
  `hfpm_entity_bind_rule_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '实体捆绑规则ID',
  `bind_type` int(2) DEFAULT NULL COMMENT '捆绑类型',
  `src_hfmd_entity_id` bigint(20) DEFAULT NULL COMMENT '源实体ID',
  `src_hfmd_entity_attr_id` bigint(20) DEFAULT NULL COMMENT '源实体属性ID',
  `dest_hfmd_entity_id` bigint(20) DEFAULT NULL COMMENT '目标实体ID',
  `dest_hfmd_entity_attr_id` bigint(20) DEFAULT NULL COMMENT '目标实体属性ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_entity_bind_rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='实体捆绑规则';


CREATE TABLE `hfpm_entity_permission` (
  `hfpm_entity_permission_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '页面事件属性ID',
  `hfmd_entity_id` bigint(20) DEFAULT NULL COMMENT '实体ID',
  `hfmd_entity_attr_id` bigint(20) DEFAULT NULL COMMENT '实体属性ID',
  `value_type` int(2) DEFAULT NULL COMMENT '值类型',
  `value` varchar(128) DEFAULT NULL COMMENT '值',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_entity_permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='实体权限';


CREATE TABLE `hfpm_field_show_type` (
  `hfpm_field_show_type_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '展示类型ID',
  `hfpm_field_show_type_code` varchar(32) DEFAULT NULL COMMENT '展示类型编码',
  `hfpm_field_show_type_name` varchar(32) DEFAULT NULL COMMENT '展示类型名称',
  `pre_str` varchar(256) DEFAULT NULL COMMENT '前缀',
  `after_str` varchar(256) DEFAULT NULL COMMENT '后缀',
  `col_span` int(2) DEFAULT NULL COMMENT '列数',
  `row_span` int(2) DEFAULT NULL COMMENT '行数',
  `width` int(11) DEFAULT NULL COMMENT '宽度',
  `height` int(11) DEFAULT NULL COMMENT '高度',
  `param1` varchar(128) DEFAULT NULL COMMENT '参数1',
  `param2` varchar(128) DEFAULT NULL COMMENT '参数2',
  `param3` varchar(128) DEFAULT NULL COMMENT '参数3',
  `param4` varchar(128) DEFAULT NULL COMMENT '参数4',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_field_show_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='展示类型';


CREATE TABLE `hfpm_module` (
  `hfpm_module_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模块ID',
  `hfpm_module_name` varchar(64) NOT NULL COMMENT '模块名称',
  `hfpm_module_code` varchar(64) NOT NULL COMMENT '模块编码',
  `hfpm_module_desc` varchar(128) DEFAULT NULL COMMENT '模块描述',
  `hfpm_program_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_module_id`),
  KEY `FK4CB7FFB0BBCC2B50` (`hfpm_program_id`)
) ENGINE=InnoDB AUTO_INCREMENT=151031110859 DEFAULT CHARSET=utf8 COMMENT='模块';


CREATE TABLE `hfpm_page` (
  `hfpm_page_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '页面ID',
  `hfpm_page_code` varchar(64) DEFAULT NULL COMMENT '页面编码',
  `hfpm_page_name` varchar(128) DEFAULT NULL COMMENT '页面名称',
  `hfpm_page_type` int(2) DEFAULT NULL COMMENT '页面类型',
  `hfpm_page_desc` varchar(128) DEFAULT NULL COMMENT '页面描述',
  `parent_hfpm_page_id` bigint(20) DEFAULT NULL COMMENT '父页面ID',
  `hfpm_program_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `hfpm_module_id` bigint(20) DEFAULT NULL COMMENT '模块ID',
  `pri` decimal(6,2) DEFAULT NULL COMMENT '优先级',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_page_id`)
) ENGINE=InnoDB AUTO_INCREMENT=198 DEFAULT CHARSET=utf8 COMMENT='页面';


CREATE TABLE `hfpm_page_cfg` (
  `hfpm_page_cfg_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '页面设置ID',
  `hfcfg_page_template_id` bigint(20) DEFAULT NULL COMMENT '页面模板ID',
  `hfpm_program_cfg_id` bigint(20) DEFAULT NULL COMMENT '项目配置ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_page_cfg_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='项目页面设置';


CREATE TABLE `hfpm_page_component` (
  `hfpm_page_component_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '页面组件ID',
  `hfpm_page_component_name` varchar(64) DEFAULT NULL COMMENT '页面组件名称',
  `hfpm_page_component_type` int(2) DEFAULT NULL COMMENT '页面组件类型',
  `hfpm_page_id` bigint(20) DEFAULT NULL COMMENT '页面ID',
  `hfpm_data_set_id` bigint(20) DEFAULT NULL COMMENT '数据集ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_page_component_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='页面组件';


CREATE TABLE `hfpm_page_entity_rel` (
  `hfpm_page_entity_rel_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '页面关联实体ID',
  `hfpm_page_id` bigint(20) DEFAULT NULL COMMENT '页面ID',
  `hfmd_entity_id` bigint(20) DEFAULT NULL COMMENT '实体ID',
  `is_main_entity` int(2) DEFAULT NULL COMMENT '是否为主实体',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_page_entity_rel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='页面关联实体';


CREATE TABLE `hfpm_page_event` (
  `hfpm_page_event_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '页面事件ID',
  `hfpm_page_id` bigint(20) DEFAULT NULL COMMENT '页面ID',
  `hfpm_event_name` varchar(32) DEFAULT NULL COMMENT '事件名称',
  `hfpm_event_monitor_object` varchar(64) DEFAULT NULL COMMENT '事件监听对象',
  `hfpm_event_monitor_object_type` varchar(64) DEFAULT NULL COMMENT '事件监听对象类型',
  `hfpm_event_type` int(2) DEFAULT NULL COMMENT '事件类型',
  `hfpm_event_effect_object` varchar(64) DEFAULT NULL COMMENT '事件作用对象',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  `hfpm_page_component_id` bigint(20) DEFAULT NULL COMMENT '页面组件ID',
  PRIMARY KEY (`hfpm_page_event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='页面事件';


CREATE TABLE `hfpm_page_event_attr` (
  `hfpm_page_event_attr_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '页面事件属性ID',
  `hfpm_page_event_id` bigint(20) DEFAULT NULL COMMENT '页面事件ID',
  `hfpm_page_event_attr_type` int(2) DEFAULT NULL COMMENT '页面事件属性类型\n            0:条件',
  `hfmd_entity_attr_id` bigint(20) DEFAULT NULL COMMENT '实体属性ID',
  `hfmd_entity_id` bigint(20) DEFAULT NULL COMMENT '实体ID',
  `value_type` int(2) DEFAULT NULL COMMENT '值类型',
  `value` varchar(128) DEFAULT NULL COMMENT '值',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_page_event_attr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='页面事件属性';


CREATE TABLE `hfpm_program` (
  `hfpm_program_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `hfpm_program_name` varchar(64) NOT NULL COMMENT '项目名称',
  `hfpm_program_code` varchar(64) NOT NULL COMMENT '项目编码',
  `hfpm_program_desc` varchar(512) DEFAULT NULL COMMENT '项目描述',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_program_id`)
) ENGINE=InnoDB AUTO_INCREMENT=151031375374 DEFAULT CHARSET=utf8 COMMENT='项目';


CREATE TABLE `hfpm_program_cfg` (
  `hfpm_program_cfg_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目配置ID',
  `show_name` varchar(128) DEFAULT NULL COMMENT '项目标题',
  `hfcfg_program_template_id` bigint(20) DEFAULT NULL COMMENT '项目模板ID',
  `hfcfg_program_skin_id` bigint(20) DEFAULT NULL COMMENT '项目皮肤ID',
  `hfcfg_login_page_id` bigint(20) DEFAULT NULL COMMENT '项目登陆页ID',
  `bg_img_url` varchar(128) DEFAULT NULL COMMENT '背景图片URL',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfpm_program_cfg_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='项目设置';


CREATE TABLE `hfsec_menu` (
  `hfsec_menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `hfsec_menu_code` varchar(64) DEFAULT NULL COMMENT '菜单编码',
  `hfsec_menu_name` varchar(128) DEFAULT NULL COMMENT '菜单名称',
  `hfsec_menu_desc` varchar(128) DEFAULT NULL COMMENT '菜单描述',
  `menu_level` int(2) DEFAULT NULL COMMENT '菜单级别',
  `icon` varchar(64) DEFAULT NULL COMMENT '图标',
  `url` varchar(128) DEFAULT NULL,
  `parent_hfsec_menu_id` bigint(20) DEFAULT NULL COMMENT '父级菜单ID',
  `hfpm_program_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `hfpm_module_id` bigint(20) DEFAULT NULL COMMENT '模块ID',
  `pri` decimal(6,2) DEFAULT NULL COMMENT '优先级',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modifier_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfsec_menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;


CREATE TABLE `hfsec_user` (
  `hfsec_user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `hfsec_user_name` varchar(64) NOT NULL COMMENT '用户名称',
  `account` varchar(64) NOT NULL COMMENT '用户账号',
  `PASSWORD` varchar(128) DEFAULT NULL COMMENT '用户密码',
  `gender` int(2) DEFAULT NULL COMMENT '性别',
  `mobile` varchar(6) DEFAULT NULL COMMENT '手机号',
  `email` int(2) DEFAULT NULL COMMENT '邮箱',
  `addr` int(2) DEFAULT NULL COMMENT '地址',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像',
  `last_login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
  `STATUS` int(2) DEFAULT NULL COMMENT '状态',
  `hfuc_org_id` bigint(20) DEFAULT NULL COMMENT '归属组织ID',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modifier_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfsec_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='用户';


CREATE TABLE `hfus_entity_attr` (
  `hfus_entity_attr_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '常用实体属性ID',
  `hfus_entity_attr_name` varchar(64) NOT NULL COMMENT '实体属性名称',
  `hfus_entity_attr_code` varchar(64) NOT NULL COMMENT '实体属性编码',
  `hfus_entity_attr_desc` varchar(128) DEFAULT NULL COMMENT '实体描述',
  `attr_type` int(2) DEFAULT NULL COMMENT '属性类型',
  `size` varchar(6) DEFAULT NULL COMMENT '大小',
  `ispk` int(2) DEFAULT NULL COMMENT '是否主键',
  `nullable` int(2) DEFAULT NULL COMMENT '是否可为空',
  `is_busi_attr` int(2) DEFAULT NULL COMMENT '是否业务属性',
  `is_redundant_attr` int(2) DEFAULT NULL COMMENT '是否冗余属性',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfus_entity_attr_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='常用实体属性';


CREATE TABLE `hfus_entity_type_relat_entity_attr` (
  `hfus_entity_type_relat_entity_attr_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `entity_type` int(2) DEFAULT NULL COMMENT '实体类型',
  `hfus_entity_attr_id` bigint(20) DEFAULT NULL COMMENT '常用实体属性ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfus_entity_type_relat_entity_attr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='实体类型关联实体属性';


CREATE TABLE `hfus_page_event` (
  `hfus_page_event_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '常用页面事件ID',
  `hfpm_event_name` varchar(32) DEFAULT NULL COMMENT '事件名称',
  `hfpm_event_type` int(2) DEFAULT NULL COMMENT '事件类型',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfus_page_event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='常用页面事件';


CREATE TABLE `hfus_program_entity_attr` (
  `hfus_program_entity_attr_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目常用实体ID',
  `hfpm_program_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `hfmd_entity_id` bigint(20) DEFAULT NULL COMMENT '实体ID',
  `hfmd_entity_attr_id` bigint(20) DEFAULT NULL COMMENT '实体属性ID',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfus_program_entity_attr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目常用实体属性';


CREATE TABLE `hfus_word_store` (
  `hfus_word_store_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '单词库ID',
  `chinese_chars` varchar(64) DEFAULT NULL COMMENT '汉字名称',
  `english_name` varchar(64) DEFAULT NULL COMMENT '英语名称',
  `english_short_name` varchar(64) DEFAULT NULL COMMENT '英文简称',
  `op_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_op_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` int(2) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`hfus_word_store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='单词库';


CREATE TABLE `sec_menu` (
  `menu_id` varchar(64) NOT NULL,
  `menu_name` varchar(128) DEFAULT NULL,
  `url` longtext,
  `par_id` varchar(64) DEFAULT NULL,
  `remark` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `sys_menu` (
  `menu_id` varchar(64) NOT NULL,
  `url` longtext,
  `pre_menu_id` varchar(64) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `sys_org` (
  `sys_org_id` varchar(64) NOT NULL,
  `sys_org_name` varchar(128) DEFAULT NULL,
  `par_org_id` varchar(64) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `pri` int(11) DEFAULT NULL,
  PRIMARY KEY (`sys_org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `sys_user` (
  `user_id` varchar(64) NOT NULL,
  `gender` int(11) DEFAULT NULL,
  `addr` longtext,
  `sign` longtext,
  `user_name` varchar(128) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `test` (
  `id` varchar(32) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL,
  `desc` varchar(128) DEFAULT NULL,
  `desc1` varchar(128) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `up_offer` (
  `offer_id` varchar(64) NOT NULL,
  `offer_type` varchar(64) DEFAULT NULL,
  `offer_desc` longtext,
  PRIMARY KEY (`offer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
