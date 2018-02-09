package com.hframework.generator.web.container;

import com.hframework.generator.web.container.bean.*;
import com.hframework.common.util.CommonUtils;
import com.hframework.common.util.StringUtils;
import com.hframework.generator.enums.HfmdEntityAttr1AttrTypeEnum;

import java.util.*;

/**
 * Created by zhangqh6 on 2015/10/25.
 */
public class HfModelContainerUtil {

    private static String[] enumEntityAttrCodeLikes = new String[]{
            "state","status","level","type","gender","flag","flg"
    };

    public static HfModelContainer[] mergerModelContainer(
            HfModelContainer baseModelContainer, HfModelContainer targetModelContainer){
        return mergerModelContainer(baseModelContainer, targetModelContainer, false);
    }

    public static HfModelContainer[] mergerModelContainer(
            HfModelContainer baseModelContainer, HfModelContainer targetModelContainer, boolean dbLevel){
        HfModelContainer[] result = new HfModelContainer[2];
        HfModelContainer  addModelContainer = new HfModelContainer();
        addModelContainer.setContainerType(HfModelContainer.TYPE_ADD);
        result[0] = addModelContainer;
        HfModelContainer  modModelContainer = new HfModelContainer();
        modModelContainer.setContainerType(HfModelContainer.TYPE_MOD);
        result[1] = modModelContainer;

        long oldProgramId = -1;
        long oldModelId = -1;

        Program dbModelContainerProgram = baseModelContainer.getProgram();
        Program targetModelContainerProgram = targetModelContainer.getProgram();
        if(dbModelContainerProgram == null) {
            addModelContainer.setProgram(targetModelContainerProgram);
        }else {
            oldProgramId = dbModelContainerProgram.getHfpmProgramId();
            if(!targetModelContainerProgram.getHfpmProgramName().equals(dbModelContainerProgram.getHfpmProgramName())){
                dbModelContainerProgram.setHfpmProgramName(targetModelContainerProgram.getHfpmProgramName());
                modModelContainer.setProgram(dbModelContainerProgram);
            }
        }

        Map<String ,Module> dbModuleMap = new HashMap<String, Module>();
        for (Module module : baseModelContainer.getModuleMap().values()) {
            dbModuleMap.put(module.getHfpmModuleCode(),module);
        }

        for (Module targetModule : targetModelContainer.getModuleMap().values()) {
            Module dbModule = dbModuleMap.get(targetModule.getHfpmModuleCode());
            if(dbModule == null) {
                addModelContainer.getModuleMap().put(targetModule.getHfpmModuleId(),targetModule);
            }else {
                oldModelId = dbModule.getHfpmModuleId();
                if(!dbModule.getHfpmModuleName().equals(targetModule.getHfpmModuleName())) {
                    dbModule.setHfpmModuleName(targetModule.getHfpmModuleName());
                    //这里需要关系是新的值，还是老的值
                    modModelContainer.getModuleMap().put(targetModule.getHfpmModuleId(),dbModule);
                }
            }
            if(oldProgramId != -1) {
                targetModule.setHfpmProgramId(oldProgramId);
            }

        }



        Map<String, Entity> dbEntityMap = baseModelContainer.getEntityMap();
        Map<String, Entity> targetEntityMap = targetModelContainer.getEntityMap();

        Map<String, Entity> addEntityMap = new LinkedHashMap<String, Entity>();
        addModelContainer.setEntityMap(addEntityMap);
        Map<String, Entity> modEntityMap = new LinkedHashMap<String, Entity>();
        modModelContainer.setEntityMap(modEntityMap);
        for (String entityCode : targetEntityMap.keySet()) {
            Entity targetEntity = targetEntityMap.get(entityCode);
            if(dbEntityMap == null) {
                dbEntityMap = new HashMap<String, Entity>();
//                addModelContainer.getEntityMap().put(entityCode, targetEntity);
//                continue;
            }
            Entity dbEntity = dbEntityMap.get(entityCode);
            if(dbEntity == null) {
                addModelContainer.getEntityMap().put(entityCode,targetEntity);
            }else if (targetEntity.getHfmdEntityName() != null &&
                    !targetEntity.getHfmdEntityName().equals(dbEntity.getHfmdEntityName())) {
                System.out.println("=> diff: curEntityName : " + dbEntity.getHfmdEntityName() +
                        "; targetEntityName : " + targetEntity.getHfmdEntityName());
                dbEntity.setHfmdEntityName(targetEntity.getHfmdEntityName());
                modModelContainer.getEntityMap().put(entityCode,dbEntity);
            }

            if(oldProgramId != -1) {
                targetEntity.setHfpmProgramId(oldProgramId);
                targetEntity.setHfpmModuleId(oldModelId);

            }
        }

        Map<String,EntityAttr> dbEntityAttrMap = baseModelContainer.getEntityAttrMap();
        Map<String,EntityAttr> targetEntityAttrMap = targetModelContainer.getEntityAttrMap();

        Map<String, EnumClass> enumClassCodeMap = baseModelContainer.getEnumClassCodeMap();
        if(enumClassCodeMap != null && targetEntityAttrMap != null) {
            for (String entityAttrCode : targetEntityAttrMap.keySet()) {
                if(enumClassCodeMap.containsKey(entityAttrCode)) {
                    targetEntityAttrMap.get(entityAttrCode).setHfmdEnumClassId(enumClassCodeMap.get(entityAttrCode).getHfmdEnumClassId());
                }
            }
        }

        for (EntityAttr entityAttr : targetEntityAttrMap.values()) {
            if (HfmdEntityAttr1AttrTypeEnum.TINYINT.getIndex() == entityAttr.getAttrType()
                    && (entityAttr.getHfmdEnumClassId() == null || entityAttr.getHfmdEnumClassId() < 0)) {
                if("gender".equals(entityAttr.getHfmdEntityAttrCode().toLowerCase())) {
                    entityAttr.setHfmdEnumClassId(3L);//设置为3-性别
                }
            }
        }

        for (EntityAttr entityAttr : targetEntityAttrMap.values()) {
            if(HfmdEntityAttr1AttrTypeEnum.TINYINT.getIndex() == entityAttr.getAttrType()
                    && (entityAttr.getHfmdEnumClassId() == null || entityAttr.getHfmdEnumClassId() < 0 )) {
                for (String enumEntityAttrCodeLike : enumEntityAttrCodeLikes) {
                    if(entityAttr.getHfmdEntityAttrCode().toLowerCase().contains(enumEntityAttrCodeLike)) {
                        entityAttr.setHfmdEnumClassId(2L);//设置为2-缺省枚举
                        break;
                    }
                }
            }
        }

        Map<String,EntityAttr> addEntityAttrMap = new HashMap<String, EntityAttr>();
        addModelContainer.setEntityAttrMap(addEntityAttrMap);
        Map<String,EntityAttr> modEntityAttrMap = new HashMap<String, EntityAttr>();
        modModelContainer.setEntityAttrMap(modEntityAttrMap);

        Map<Long, List<String>> dbKeyRewriteCacheMap = new HashMap<Long, List<String>>();
        for (String emtityAttrCode : targetEntityAttrMap.keySet()) {
            EntityAttr targetEntityAttr = targetEntityAttrMap.get(emtityAttrCode);
            if(targetEntityAttr.getRelHfmdEntityAttrId() != null) {
                if(!dbKeyRewriteCacheMap.containsKey(targetEntityAttr.getRelHfmdEntityAttrId())) {
                    dbKeyRewriteCacheMap.put(targetEntityAttr.getRelHfmdEntityAttrId(), new ArrayList<String>());
                }
                dbKeyRewriteCacheMap.get(targetEntityAttr.getRelHfmdEntityAttrId()).add(emtityAttrCode);
            }
        }
        for (String emtityAttrCode : targetEntityAttrMap.keySet()) {
            EntityAttr targetEntityAttr = targetEntityAttrMap.get(emtityAttrCode);
            if(dbKeyRewriteCacheMap.containsKey(targetEntityAttr.getHfmdEntityAttrId())) {
                List<String> strings = dbKeyRewriteCacheMap.get(targetEntityAttr.getHfmdEntityAttrId());
                for (String string : strings) {
                    modModelContainer.getRelEntityAttr2AttrMapper().put(string, emtityAttrCode);
                    addModelContainer.getRelEntityAttr2AttrMapper().put(string, emtityAttrCode);
                }
            }
        }

        //针对于外键优先替换
        overrideRelHfmdEntityAttrIdIfDbExists(dbEntityAttrMap, targetEntityAttrMap, modModelContainer);

        for (String emtityAttrCode : targetEntityAttrMap.keySet()) {
            EntityAttr targetEntityAttr = targetEntityAttrMap.get(emtityAttrCode);
            if(dbEntityAttrMap == null) {
                dbEntityAttrMap = new HashMap<String, EntityAttr>();
//                addModelContainer.getEntityAttrMap().put(emtityAttrCode, targetEntityAttr);
//                continue;
            }
            EntityAttr dbEntityAttr = dbEntityAttrMap.get(emtityAttrCode);
            if(dbEntityAttr == null) {
                dbEntityAttr = dbEntityAttrMap.get(emtityAttrCode.replace(targetEntityAttr.getHfmdEntityAttrCode(),
                        targetEntityAttr.getHfmdEntityAttrCode().toUpperCase()));
            }
            if(dbEntityAttr == null) {
                dbEntityAttr = dbEntityAttrMap.get(emtityAttrCode.replace(targetEntityAttr.getHfmdEntityAttrCode(),
                        targetEntityAttr.getHfmdEntityAttrCode().toLowerCase()));

            }


            if(targetEntityAttr != null && targetEntityAttr.getAttrType() != null && StringUtils.isBlank(targetEntityAttr.getSize())) {
                Integer defaultSize = HfmdEntityAttr1AttrTypeEnum.getDefaultSize(targetEntityAttr.getAttrType());
                if(defaultSize != null && defaultSize > 0) {
                    targetEntityAttr.setSize(String.valueOf(defaultSize));
                }
            }
            Map<String,String> oldProperty = new HashMap<String, String>();
            if(dbEntityAttr == null) {
                addModelContainer.getEntityAttrMap().put(emtityAttrCode, targetEntityAttr);
            }else {
                boolean changed = false;
                if(dbEntityAttr.getAttrType() == null ) {
                    System.out.println();
                }
                if (checkEntityNameDiff(dbEntityAttr.getHfmdEntityAttrName(), targetEntityAttr.getHfmdEntityAttrName())) {
                    System.out.println("=> diff: entityCode : " + emtityAttrCode + "; curAttrName : " + dbEntityAttr.getHfmdEntityAttrName() +
                            "; targetAttrName : " + targetEntityAttr.getHfmdEntityAttrName());
                    oldProperty.put("name",dbEntityAttr.getHfmdEntityAttrName());
                    changed = true;
                }
                if (checkEntityNameDiff(dbEntityAttr.getHfmdEntityAttrDesc(), targetEntityAttr.getHfmdEntityAttrDesc())) {
                    System.out.println("=> diff: entityCode : " + emtityAttrCode + "; curAttrDesc : " + dbEntityAttr.getHfmdEntityAttrDesc() +
                            "; targetAttrDesc : " + targetEntityAttr.getHfmdEntityAttrDesc());
                    oldProperty.put("desc",dbEntityAttr.getHfmdEntityAttrDesc());
                    changed = true;
                }
                if (isDiff(dbEntityAttr.getIspk(), targetEntityAttr.getIspk())) {
                    System.out.println("=> diff: entityCode : " + emtityAttrCode + "; curIspk : " + dbEntityAttr.getIspk() +
                            "; targetIspk : " + targetEntityAttr.getIspk());
                    oldProperty.put("ispk", String.valueOf(dbEntityAttr.getIspk()));
                    changed = true;
                }
                if (!dbEntityAttr.getNullable().equals(targetEntityAttr.getNullable())
                        && dbEntityAttr.getIspk() !=1  && targetEntityAttr.getIspk() !=1) {
                    System.out.println("=> diff entityCode : " + emtityAttrCode + "; :curNullable : " + dbEntityAttr.getNullable() +
                            "; targetNullable : " + targetEntityAttr.getNullable());
                    oldProperty.put("nullable", String.valueOf(dbEntityAttr.getNullable()));
                    changed = true;
                }
                if (isDiff(dbEntityAttr.getSize(), targetEntityAttr.getSize())) {
                    System.out.println("=> diff:curSize : " + dbEntityAttr.getSize() +
                            "; targetSize : " + targetEntityAttr.getSize());
                    oldProperty.put("size", String.valueOf(dbEntityAttr.getSize()));
                    changed = true;
                }
                if (isDiff(dbEntityAttr.getHfmdEnumClassId(), targetEntityAttr.getHfmdEnumClassId())
                        && targetEntityAttr.getHfmdEnumClassId() != null) {
                    System.out.println("=> diff:curHfmdEnumClassId : " + dbEntityAttr.getHfmdEnumClassId() +
                            "; targetHfmdEnumClassId : " + targetEntityAttr.getHfmdEnumClassId());
                    oldProperty.put("enumClass", String.valueOf(dbEntityAttr.getHfmdEnumClassId()));
                    changed = true;
                }
                if (dbEntityAttr.getAttrType() == null && !dbEntityAttr.getAttrType().equals(targetEntityAttr.getAttrType())) {
                    if(dbEntityAttr.getAttrType() != null && dbEntityAttr.getAttrType() == HfmdEntityAttr1AttrTypeEnum.NUMERIC.getIndex()
                            && targetEntityAttr.getAttrType() == HfmdEntityAttr1AttrTypeEnum.DECIMAL.getIndex()) {
                        continue;
                    }

                    if(targetEntityAttr.getAttrType() != null && targetEntityAttr.getAttrType() == HfmdEntityAttr1AttrTypeEnum.NUMERIC.getIndex()
                            && dbEntityAttr.getAttrType() == HfmdEntityAttr1AttrTypeEnum.DECIMAL.getIndex()) {
                        continue;
                    }
                    System.out.println("=> diff: entityCode : " + emtityAttrCode + "; curAttrType : " + dbEntityAttr.getAttrType() +
                            "; targetAttrType : " + targetEntityAttr.getAttrType());
                    oldProperty.put("attrType", HfmdEntityAttr1AttrTypeEnum.getName(targetEntityAttr.getAttrType()));
                    changed = true;
                }

                if(changed && targetEntityAttr.getAttrType() != null) {
                    modModelContainer.getEntityAttrChangeTypeMap().put(dbEntityAttr, HfModelContainer.AttrChangeType.FIELD);
                }

                if (isDiff(dbEntityAttr.getRelHfmdEntityAttrId(), targetEntityAttr.getRelHfmdEntityAttrId())) {
                    System.out.println("=> diff: entityCode : " + emtityAttrCode + "; curRelHfmdEntityAttrId : " + dbEntityAttr.getRelHfmdEntityAttrId() +
                            "; targetRelHfmdEntityAttrId : " + targetEntityAttr.getRelHfmdEntityAttrId());
                    if(modModelContainer.getEntityAttrChangeTypeMap().get(dbEntityAttr) != null
                            && modModelContainer.getEntityAttrChangeTypeMap().get(dbEntityAttr) == HfModelContainer.AttrChangeType.FIELD ) {
                        modModelContainer.getEntityAttrChangeTypeMap().put(dbEntityAttr, HfModelContainer.AttrChangeType.FULL);
                    }else {
                        modModelContainer.getEntityAttrChangeTypeMap().put(dbEntityAttr, HfModelContainer.AttrChangeType.FK);
                    }
                    oldProperty.put("relAttrId", String.valueOf(dbEntityAttr.getHfmdEnumClassId()));
                    changed = true;
                }

                if(changed) {
                    if(modModelContainer.getEntityAttrChangeTypeMap().get(dbEntityAttr) != null) {
                        if(modModelContainer.getEntityAttrChangeTypeMap().get(dbEntityAttr).containField()) {
                            dbEntityAttr.setHfmdEntityAttrName(targetEntityAttr.getHfmdEntityAttrName());
                            dbEntityAttr.setIspk(targetEntityAttr.getIspk());
                            dbEntityAttr.setAttrType(targetEntityAttr.getAttrType());
                            dbEntityAttr.setPri(targetEntityAttr.getPri());
                            dbEntityAttr.setHfmdEntityAttrDesc(targetEntityAttr.getHfmdEntityAttrDesc());
                            dbEntityAttr.setSize(targetEntityAttr.getSize());
                            dbEntityAttr.setNullable(targetEntityAttr.getNullable());
                            dbEntityAttr.setHfmdEnumClassId(targetEntityAttr.getHfmdEnumClassId());
                            dbEntityAttr.setOldProperty(oldProperty);
                        }
                        if(modModelContainer.getEntityAttrChangeTypeMap().get(dbEntityAttr).containFk()) {
                            dbEntityAttr.setRelHfmdEntityAttrId(targetEntityAttr.getRelHfmdEntityAttrId());
                        }
                        modModelContainer.getEntityAttrMap().put(emtityAttrCode,dbEntityAttr);
                    }
                }
            }

            Entity hfmdEntity = dbEntityMap.get(emtityAttrCode.split("\\.")[0].trim());
            if(hfmdEntity == null) {
                hfmdEntity = targetEntityMap.get(emtityAttrCode.split("\\.")[0].trim());
            }


            if(oldProgramId != -1 && hfmdEntity != null) {
                targetEntityAttr.setHfpmProgramId(hfmdEntity.getHfpmProgramId());
                targetEntityAttr.setHfpmModuleId(hfmdEntity.getHfpmModuleId());
                targetEntityAttr.setHfmdEntityId(hfmdEntity.getHfmdEntityId());
            }
            if(StringUtils.isBlank(targetEntityAttr.getHfmdEntityAttrName())) {
                targetEntityAttr.setHfmdEntityAttrName("");
            }
        }
        return result;
    }

    private static boolean isDiff(Long originValue, Long targetValue) {
        if(originValue == null && targetValue == null) {
            return false;
        }
        if(originValue == null || targetValue == null) {
            return true;
        }
        return !originValue.equals(targetValue);
    }

    private static boolean isDiff(Integer originValue, Integer targetValue) {
        if(originValue == null && targetValue == null) {
            return false;
        }
        if(originValue == null || targetValue == null) {
            return true;
        }
        return !originValue.equals(targetValue);
    }

    private static boolean isDiff(String originValue, String targetValue) {
        if(StringUtils.isBlank(originValue) && StringUtils.isBlank(targetValue)) {
            return false;
        }

        if(StringUtils.isBlank(originValue) || StringUtils.isBlank(targetValue)) {
            return true;
        }
        return !originValue.trim().equals(targetValue.trim());
    }


    private static void overrideRelHfmdEntityAttrIdIfDbExists(Map<String, EntityAttr> dbEntityAttrMap,
                                                              Map<String, EntityAttr> targetEntityAttrMap,
                                                              HfModelContainer modModelContainer) {
        if(dbEntityAttrMap != null) {
            Map<Long, Long> dbKeyRewriteMap = new HashMap<Long, Long>();
            for (String emtityAttrCode : targetEntityAttrMap.keySet()) {
                EntityAttr targetEntityAttr = targetEntityAttrMap.get(emtityAttrCode);
                if(targetEntityAttr.getRelHfmdEntityAttrId() != null) {
                    dbKeyRewriteMap.put(targetEntityAttr.getRelHfmdEntityAttrId(), null);

                }
            }
            for (String emtityAttrCode : targetEntityAttrMap.keySet()) {
                EntityAttr targetEntityAttr = targetEntityAttrMap.get(emtityAttrCode);
                if(dbKeyRewriteMap.containsKey(targetEntityAttr.getHfmdEntityAttrId())) {
                    if(dbEntityAttrMap.containsKey(emtityAttrCode)) {//该主键是别的表的外检，且数据库已经存在该主键
                        EntityAttr dbEntityAttr = dbEntityAttrMap.get(emtityAttrCode);
                        dbKeyRewriteMap.put(targetEntityAttr.getHfmdEntityAttrId(), dbEntityAttr.getHfmdEntityAttrId());
                    }
                }
            }
            for (EntityAttr targetEntityAttr : targetEntityAttrMap.values()) {
                if(targetEntityAttr.getRelHfmdEntityAttrId() != null) {
                    Long dbKey = dbKeyRewriteMap.get(targetEntityAttr.getRelHfmdEntityAttrId());
                    if(dbKey != null) {
                        targetEntityAttr.setRelHfmdEntityAttrId(dbKey);
                    }
                }
            }
        }
    }

    private static boolean checkEntityNameDiff(String s1, String s2) {
        if(StringUtils.isBlank(s1) && StringUtils.isBlank(s2)) {
            return false;
        }

        if(StringUtils.isBlank(s1) || StringUtils.isBlank(s2)) {
            return true;
        }
        s1 = s1.trim().replace(":", " ").replace("\\n"," ");
        s2 = s2.trim().replace(":", " ").replace("\\n"," ");
        if(s1.contains(" ")) {
            s1 = s1.substring(0, s1.indexOf(" "));
        }

        if(s2.contains(" ")) {
            s2 = s2.substring(0, s2.indexOf(" "));
        }
//        System.out.println(s1 +"|" + s2);
        return !s1.equals(s2);
    }

    public static HfModelContainer[] mergerEntityToDataSet(HfModelContainer[] resultModelContainers, HfModelContainer dbModelContainer) {

        if(resultModelContainers == null) {
            return null;
        }

        HfModelContainer addModelContainer = resultModelContainers[0];
        resetReallyDBEntity(addModelContainer, dbModelContainer);
        mergerModelContainerSelf(addModelContainer, dbModelContainer);

        HfModelContainer modModelContainer = resultModelContainers[1];
        resetReallyDBEntity(modModelContainer,dbModelContainer);
        mergerModelContainerSelf(modModelContainer, dbModelContainer);


        return resultModelContainers;
    }

    private static void resetReallyDBEntity(HfModelContainer addModelContainer, HfModelContainer dbModelContainer) {
        Map<String, Entity> entityMap = addModelContainer.getEntityMap();
        for (String entityCode : entityMap.keySet()) {
            if(dbModelContainer.getEntity(entityCode) != null) {
                entityMap.put(entityCode, dbModelContainer.getEntity(entityCode) );
            }
        }

        Map<String, EntityAttr> entityAttrMap = addModelContainer.getEntityAttrMap();
        for (String entityAttrCode : entityAttrMap.keySet()) {
            if(dbModelContainer.getEntityAttrMap().get(entityAttrCode) != null) {
                entityAttrMap.put(entityAttrCode, dbModelContainer.getEntityAttrMap().get(entityAttrCode));
            }
        }
    }

    public static HfModelContainer[] mergerEntityToDataSetReturnOnly(HfModelContainer[] resultModelContainers, HfModelContainer dbModelContainer) {

        if(resultModelContainers == null) {
            return null;
        }

        HfModelContainer addModelContainer = resultModelContainers[0];
        resetReallyDBEntity(addModelContainer, dbModelContainer);

        mergerModelContainerSelf(addModelContainer, dbModelContainer);
        //避免重复执行
        Map<String, DataSet> dataSetMap = addModelContainer.getDataSetMap();
        Iterator<Map.Entry<String, DataSet>> iterator = dataSetMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, DataSet> next = iterator.next();
            String dataSetCode = next.getKey();
            if(dbModelContainer.getDataSetMap().containsKey(dataSetCode)) {
                iterator.remove();
                addModelContainer.getDataFieldListMap().remove(dataSetCode);
            }
        }


        HfModelContainer modModelContainer = resultModelContainers[1];
        resetReallyDBEntity(modModelContainer, dbModelContainer);
        mergerModelContainerSelf(modModelContainer, dbModelContainer);


        return new HfModelContainer[]{getDataSetOnly(addModelContainer), getDataSetOnly(modModelContainer)};
    }

    private static HfModelContainer getDataSetOnly(HfModelContainer originContainer) {
        HfModelContainer result = new HfModelContainer();
        result.setDataSetMap(originContainer.getDataSetMap());
        result.setDataFieldListMap(originContainer.getDataFieldListMap());
        return result;
    }

    private static void mergerModelContainerSelf(HfModelContainer modelContainer, HfModelContainer dbModelContainer) {
        if (modelContainer == null) {
            return;
        }

        Map<String, Entity> entityMap = modelContainer.getEntityMap();
        Map<String, EntityAttr> entityAttrMap = modelContainer.getEntityAttrMap();

        Map<String, DataSet> dataSetMap = new HashMap<String, DataSet>();
        modelContainer.setDataSetMap(dataSetMap);
        if (entityMap != null) {
            for (String entityCode : entityMap.keySet()) {
                Entity hfmdEntity = entityMap.get(entityCode);
                dataSetMap.put(entityCode,getDataSetFromEntity(hfmdEntity));
                dataSetMap.put(entityCode + "_DS4Q", getQryDataSetFromEntity(hfmdEntity));


            }
        }

        Map<String, List<DataField>> dataFieldListMap = new HashMap<String, List<DataField>>();
        modelContainer.setDataFieldListMap(dataFieldListMap);
        if (entityAttrMap != null) {
            for (String entityAttrEntityCode : entityAttrMap.keySet()) {
                String entityCode = entityAttrEntityCode.substring(0, entityAttrEntityCode.indexOf("."));
                EntityAttr hfmdEntityAttr = entityAttrMap.get(entityAttrEntityCode);
                if(!dataFieldListMap.containsKey(entityCode)) {
                    dataFieldListMap.put(entityCode,new ArrayList<DataField>());
                }

                DataSet dataSet;
                if(dataSetMap.containsKey(entityCode)) {
                    dataSet = dataSetMap.get(entityCode);
                }else {
                    dataSet =  dbModelContainer.getDataSetMap().get(entityCode);
                }

                //脏数据
                if(dataSet == null) {
                    continue;
                }

                dataFieldListMap.get(entityCode).add(getDataFieldFromEntityAttr(hfmdEntityAttr,dataSet));

                if(!dataFieldListMap.containsKey(entityCode + "_DS4Q")) {
                    dataFieldListMap.put(entityCode+"_DS4Q",new ArrayList<DataField>());
                }

                if(dataSetMap.containsKey(entityCode+"_DS4Q")) {
                    dataSet = dataSetMap.get(entityCode+"_DS4Q");
                }else {
                    dataSet =  dbModelContainer.getDataSetMap().get(entityCode+"_DS4Q");
                }

                dataFieldListMap.get(entityCode+"_DS4Q").addAll(getDS4QryDataFieldFromEntityAttr(hfmdEntityAttr, dataSet));
            }
        }
    }

    public static DataSet getDataSetFromEntity(Entity hfmdEntity) {
        DataSet hfpmDataSet = new DataSet( CommonUtils.uuidL(),
                hfmdEntity.getHfmdEntityName()+"【默认】",
                hfmdEntity.getHfmdEntityCode(),
                hfmdEntity.getHfmdEntityId(),
                hfmdEntity.getHfpmProgramId(),
                hfmdEntity.getOpId(),
                hfmdEntity.getCreateTime(),
                hfmdEntity.getModifyOpId(),
                hfmdEntity.getModifyTime(),
                hfmdEntity.getDelFlag());
        return hfpmDataSet;
    }

    public static DataSet getQryDataSetFromEntity(Entity hfmdEntity) {
        DataSet hfpmDataSet = new DataSet( CommonUtils.uuidL(),
                hfmdEntity.getHfmdEntityName()+"【查询】",
                hfmdEntity.getHfmdEntityCode()+"_DS4Q",
                hfmdEntity.getHfmdEntityId(),
                hfmdEntity.getHfpmProgramId(),
                hfmdEntity.getOpId(),
                hfmdEntity.getCreateTime(),
                hfmdEntity.getModifyOpId(),
                hfmdEntity.getModifyTime(),
                hfmdEntity.getDelFlag());
        return hfpmDataSet;
    }

    public static DataField getDataFieldFromEntityAttr(EntityAttr hfmdEntityAttr, DataSet dataSet) {
        DataField hfpmDataField= new DataField();
        hfpmDataField.setHfpmDataFieldId(CommonUtils.uuidL());
        hfpmDataField.setHfpmDataFieldCode(hfmdEntityAttr.getHfmdEntityAttrCode());
        hfpmDataField.setHfpmFieldShowTypeId(getFieldShowTypeIdByEntityAttr(hfmdEntityAttr));
        String showCodes = getFieldShowCodeByEntityAttr(hfmdEntityAttr);
//        hfpmDataField.setFieldShowCode(getFieldShowCodeByEntityAttr(hfmdEntityAttr));
        hfpmDataField.setCreateEditAuth(Byte.valueOf(String.valueOf(showCodes.charAt(0))));
        hfpmDataField.setUpdateEditAuth(Byte.valueOf(String.valueOf(showCodes.charAt(1))));
        hfpmDataField.setListShowAuth(Byte.valueOf(String.valueOf(showCodes.charAt(2))));
        hfpmDataField.setDetailShowAuth(Byte.valueOf(String.valueOf(showCodes.charAt(2))));

        hfpmDataField.setHfmdEntityId(hfmdEntityAttr.getHfmdEntityId());
        hfpmDataField.setHfmdEntityAttrId(hfmdEntityAttr.getHfmdEntityAttrId());
        hfpmDataField.setDataGetMethod(0);
        hfpmDataField.setHfpmDataFieldName(hfmdEntityAttr.getHfmdEntityAttrName());
        hfpmDataField.setHfpmDataSetId(dataSet.getHfpmDataSetId());
        hfpmDataField.setPri(hfmdEntityAttr.getPri());
        hfpmDataField.setOpId(hfmdEntityAttr.getOpId());
        hfpmDataField.setCreateTime(hfmdEntityAttr.getCreateTime());
        hfpmDataField.setModifyOpId(hfmdEntityAttr.getModifyOpId());
        hfpmDataField.setModifyTime(hfmdEntityAttr.getModifyTime());
        hfpmDataField.setDelFlag(hfmdEntityAttr.getDelFlag());

        return hfpmDataField;
    }

        //v1.0
//    public static List<DataField> getDS4QryDataFieldFromEntityAttr(EntityAttr hfmdEntityAttr, DataSet dataSet) {
//
//        List<DataField> result = new ArrayList<DataField>();
//
//        DataField dataFieldFromEntityAttr = null;
//        if("pri".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())) {
//            return result;
//        }else if("create_time".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())
//                || "modify_time".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())) {
//            dataFieldFromEntityAttr = getDataFieldFromEntityAttr(hfmdEntityAttr, dataSet);
//            dataFieldFromEntityAttr.setHfpmDataFieldName(
//                    dataFieldFromEntityAttr.getHfpmDataFieldName().replaceAll("时间","开始时间"));
//            dataFieldFromEntityAttr.setHfpmDataFieldCode(
//                    dataFieldFromEntityAttr.getHfpmDataFieldCode() + "_GEQ");
////            dataFieldFromEntityAttr.setFieldShowCode("222");
//            dataFieldFromEntityAttr.setCreateEditAuth((byte) 2);
//            dataFieldFromEntityAttr.setUpdateEditAuth((byte) 2);
//            dataFieldFromEntityAttr.setListShowAuth((byte) 1);
//            dataFieldFromEntityAttr.setDetailShowAuth((byte) 1);
//            result.add(dataFieldFromEntityAttr);
//            dataFieldFromEntityAttr = getDataFieldFromEntityAttr(hfmdEntityAttr, dataSet);
//            dataFieldFromEntityAttr.setHfpmDataFieldName(
//                    dataFieldFromEntityAttr.getHfpmDataFieldName().replaceAll("时间", "结束时间"));
//            dataFieldFromEntityAttr.setHfpmDataFieldCode(
//                    dataFieldFromEntityAttr.getHfpmDataFieldCode() + "_LEQ");
////            dataFieldFromEntityAttr.setFieldShowCode("222");
//            dataFieldFromEntityAttr.setCreateEditAuth((byte) 2);
//            dataFieldFromEntityAttr.setUpdateEditAuth((byte) 2);
//            dataFieldFromEntityAttr.setListShowAuth((byte) 1);
//            dataFieldFromEntityAttr.setDetailShowAuth((byte) 1);
//            result.add(dataFieldFromEntityAttr);
//        }else {
//            dataFieldFromEntityAttr = getDataFieldFromEntityAttr(hfmdEntityAttr, dataSet);
////            dataFieldFromEntityAttr.setFieldShowCode("222");
//            dataFieldFromEntityAttr.setCreateEditAuth((byte) 2);
//            dataFieldFromEntityAttr.setUpdateEditAuth((byte) 2);
//            dataFieldFromEntityAttr.setListShowAuth((byte)1);
//            dataFieldFromEntityAttr.setDetailShowAuth((byte)1);
//            result.add(dataFieldFromEntityAttr);
//        }
//
//        return result;
//    }


    //v2.0
    public static List<DataField> getDS4QryDataFieldFromEntityAttr(EntityAttr hfmdEntityAttr, DataSet dataSet) {

        List<DataField> result = new ArrayList<DataField>();

        DataField dataFieldFromEntityAttr = null;
        if("pri".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())) {
            return result;
        }else if("create_time".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())
                || "modify_time".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())
                || "ctime".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())
                || "mtime".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())) {
            dataFieldFromEntityAttr = getDataFieldFromEntityAttr(hfmdEntityAttr, dataSet);
            dataFieldFromEntityAttr.setHfpmDataFieldName(dataFieldFromEntityAttr.getHfpmDataFieldName());
            dataFieldFromEntityAttr.setHfpmDataFieldCode(dataFieldFromEntityAttr.getHfpmDataFieldCode());
//            dataFieldFromEntityAttr.setFieldShowCode("222");
            dataFieldFromEntityAttr.setCreateEditAuth((byte) 2);
            dataFieldFromEntityAttr.setUpdateEditAuth((byte) 2);
            dataFieldFromEntityAttr.setListShowAuth((byte) 1);
            dataFieldFromEntityAttr.setDetailShowAuth((byte) 1);
            result.add(dataFieldFromEntityAttr);
        }else {
            dataFieldFromEntityAttr = getDataFieldFromEntityAttr(hfmdEntityAttr, dataSet);
            if(hfmdEntityAttr.getAttrType() == HfmdEntityAttr1AttrTypeEnum.VARCHAR.getIndex() && "1".equals(dataFieldFromEntityAttr.getHfpmFieldShowTypeId())){
                dataFieldFromEntityAttr.setHfpmDataFieldCode(dataFieldFromEntityAttr.getHfpmDataFieldCode() + "_LKE");
            }
//            dataFieldFromEntityAttr.setFieldShowCode("222");
            dataFieldFromEntityAttr.setCreateEditAuth((byte) 2);
            dataFieldFromEntityAttr.setUpdateEditAuth((byte) 2);
            dataFieldFromEntityAttr.setListShowAuth((byte)1);
            dataFieldFromEntityAttr.setDetailShowAuth((byte)1);
            result.add(dataFieldFromEntityAttr);
        }

        return result;
    }

    /**
     *  新建/修改：固定枚举值个数大于2个为下拉框,
        新建/修改：固定枚举值个数不大于2个为单选框
        新建/修改：关联配置表下拉框选择
        新建/修改：关联业务表弹出框选择
        新建/修改：默认都为input框
     * @param hfmdEntityAttr
     * @return
     */
    public static String getFieldShowTypeIdByEntityAttr(EntityAttr hfmdEntityAttr) {

        if(hfmdEntityAttr.getHfmdEnumClassId()!= null && hfmdEntityAttr.getHfmdEnumClassId() > 0) {//枚举值对象
            return "2";//TODO
        }else if(hfmdEntityAttr.getRelHfmdEntityAttrId()!= null && hfmdEntityAttr.getRelHfmdEntityAttrId() > 0){ //外键对象
            return "2";//TODO
        }else if(hfmdEntityAttr.getAttrType() == HfmdEntityAttr1AttrTypeEnum.DATETIME.getIndex()
                || hfmdEntityAttr.getAttrType() == HfmdEntityAttr1AttrTypeEnum.DATE.getIndex()
                ||(hfmdEntityAttr.getAttrType() == HfmdEntityAttr1AttrTypeEnum.INT.getIndex() && "ctime".equals(hfmdEntityAttr.getHfmdEntityAttrName()))
                ||(hfmdEntityAttr.getAttrType() == HfmdEntityAttr1AttrTypeEnum.INT.getIndex() && "mtime".equals(hfmdEntityAttr.getHfmdEntityAttrName()))) {
            return "3";//TODO
        } else {
            return "1";//TODO
        }
    }

    public static String getFieldShowCodeByEntityAttr(EntityAttr hfmdEntityAttr) {

        if(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase().endsWith("_id") && hfmdEntityAttr.getIspk() != null && hfmdEntityAttr.getIspk() == 1) {
            return "011";
        }else if("create_time".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())
                || "ctime".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())
                || "op_id".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())
                || "creator_id".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())) {
            return "011";
        }else if("modify_op_id".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())
                || "mtime".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())
                || "modify_time".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())
                || "modifier_id".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())) {
            return "001";
        }else if("del_flag".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())) {
            return "021";
        }else if("pri".equals(hfmdEntityAttr.getHfmdEntityAttrCode().toLowerCase())) {
            return "000";
        }else {
            return "221";
        }
    }

    public static List<String> getSql(List<Map<String, Object>> result, String tableName, boolean merge) {
        if(result == null || result.size() == 0) return new ArrayList<String>();

        Map<String, List<String>> tmpMap = new LinkedHashMap<String, List<String>>();

        for (Map<String, Object> stringStringMap : result) {
            String[] keyValuePair = getKeyValuePair(stringStringMap);
            if(!tmpMap.containsKey(keyValuePair[0])) {
                tmpMap.put(keyValuePair[0], new ArrayList<String>());
            }
            tmpMap.get(keyValuePair[0]).add(keyValuePair[1]);
        }
        List<String> sqls = new ArrayList<String>();
        for (Map.Entry<String, List<String>> entry : tmpMap.entrySet()) {
            StringBuffer sql = new StringBuffer().append("insert into " +  tableName + entry.getKey() + " values ");
            if(merge) {
                for (String values : entry.getValue()) {
                    sql.append(values).append(",");
                }
                sqls.add(sql.substring(0, sql.length() - 1));
            }else {
                for (String values : entry.getValue()) {
                    sqls.add(sql.toString() + values );
                }
            }

        }
        Collections.sort(sqls);
        return sqls;
    }

    private static String[] getKeyValuePair(Map<String, Object> stringStringMap) {
        List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(stringStringMap.entrySet());
        Collections.sort(infoIds,
                new Comparator<Map.Entry<String, Object>>() {
                    public int compare(Map.Entry<String, Object> o1,
                                       Map.Entry<String, Object> o2) {
                        return (o1.getKey()).toString().compareTo(
                                o2.getKey());
                    }
                });
        String key = "";
        String value = "";
        for (int i = 0; i < infoIds.size(); i++) {
            Map.Entry<String, Object> item = infoIds.get(i);
            key += ("`" + item.getKey() + "`,");
            if (item.getValue() instanceof Long || item.getValue() instanceof Integer || item.getValue() instanceof Short
                    || item.getValue() instanceof Double || item.getValue() instanceof Float) {
                value += (item.getValue() + ",");
            }else {
                value += ("'" + item.getValue() + "',");
            }

        }

        key = "(" +  key.substring(0,key.length()-1)+ ")";
        value ="(" +  value.substring(0,value.length()-1) + ")";

        return new String[]{key, value};
    }
    public static List<String> getSql(HfModelContainer addContainer, HfModelContainer modifyContainer) {
        return getSql(addContainer, modifyContainer, true, false);
    }

    public static List<String> getSql(HfModelContainer addContainer, HfModelContainer modifyContainer, boolean containFk, boolean backQuote) {

        List<String> result = new ArrayList<String>();

        final ArrayList entityOrderedList = new ArrayList(addContainer.getEntityMap().values());
        TreeMap<Entity, List<EntityAttr>> newTableMap = new TreeMap<Entity, List<EntityAttr>>(new Comparator<Entity>() {
            public int compare(Entity o1, Entity o2) {
                return entityOrderedList.indexOf(o1) - entityOrderedList.indexOf(o2);
            }
        });

        Map<String, EntityAttr> entityAttrMap = addContainer.getEntityAttrMap();
        List<String> tempList = new ArrayList<String>();

        for (String key : entityAttrMap.keySet()) {

            EntityAttr entityAttr = entityAttrMap.get(key);
            String entityName = key.substring(0, key.indexOf("."));

            if(addContainer.getEntityMap().containsKey(entityName)) {
                Entity hfmdEntity = addContainer.getEntityMap().get(entityName);
                if(!newTableMap.containsKey(hfmdEntity)) {
                    newTableMap.put(hfmdEntity, new ArrayList<EntityAttr>());
                }
                newTableMap.get(hfmdEntity).add(entityAttr);
            }else{
                StringBuffer sql = new StringBuffer();
                sql.append("alter table " + backQuote(entityName,backQuote) + " add column " + getColumnInfo(entityAttr, backQuote) + ";");
                tempList.add(sql.toString());
            }
        }
        Collections.sort(tempList);
        result.addAll(tempList);

        tempList = new ArrayList<String>();
        for (Entity hfmdEntity : newTableMap.keySet()) {
            StringBuffer sql = new StringBuffer();
            sql.append("create table " + backQuote(hfmdEntity.getHfmdEntityCode(), backQuote) + "(").append("\n");
            List<EntityAttr> hfmdEntityAttrs = newTableMap.get(hfmdEntity);
            Collections.sort(hfmdEntityAttrs, new Comparator<EntityAttr>() {
                public int compare(EntityAttr o1, EntityAttr o2) {
                    return o1.getPri().compareTo(o2.getPri());
                }
            });
            for (EntityAttr hfmdEntityAttr : hfmdEntityAttrs) {
                System.out.println("->" + hfmdEntity.getHfmdEntityCode());
                sql.append("   " + getColumnInfo(hfmdEntityAttr, backQuote)).append(",").append("\n");
            }
            sql.delete(sql.length() - 2, sql.length()).append(")");
            if(StringUtils.isNotBlank(hfmdEntity.getHfmdEntityName())) {
                sql.append(" comment '" + hfmdEntity.getHfmdEntityName() + "'");
            }
            sql.append(";");
            tempList.add(sql.toString());
        }
//        Collections.sort(tempList);
        result.addAll(0, tempList);


        tempList = new ArrayList<String>();
        for (Entity hfmdEntity : modifyContainer.getEntityMap().values()) {
            StringBuffer sql = new StringBuffer();
            sql.append("alter table " + backQuote(hfmdEntity.getHfmdEntityCode(),backQuote) + " comment '" + hfmdEntity.getHfmdEntityName() + "';");
            tempList.add(sql.toString());
        }
        Collections.sort(tempList);
        result.addAll(tempList);

        entityAttrMap = modifyContainer.getEntityAttrMap();
        tempList = new ArrayList<String>();
        for (String key : entityAttrMap.keySet()) {
            EntityAttr entityAttr = entityAttrMap.get(key);
            if(modifyContainer.getEntityAttrChangeTypeMap().containsKey(entityAttr)
                    && modifyContainer.getEntityAttrChangeTypeMap().get(entityAttr).containField()) {
                Map<String, String> oldProperty = entityAttr.getOldProperty();
                if(oldProperty.containsKey("enumClass")) oldProperty.remove("enumClass");
                if(!oldProperty.isEmpty()) {
                    String entityName = key.substring(0, key.indexOf("."));
                    StringBuffer sql = new StringBuffer();
                    sql.append("alter table " + backQuote(entityName,backQuote)  + " modify column " + getColumnInfo(entityAttr, backQuote) + ";");
                    sql.append(" --").append(oldProperty);
                    tempList.add(sql.toString());
                }

            }
        }
        Collections.sort(tempList);
        result.addAll(tempList);

        tempList = new ArrayList<String>();
        entityAttrMap = addContainer.getEntityAttrMap();
        for (String key : entityAttrMap.keySet()) {
            EntityAttr entityAttr = entityAttrMap.get(key);
            if(entityAttr.getRelHfmdEntityAttrId() != null && entityAttr.getRelHfmdEntityAttrId() > 0 && containFk) {

                String entityName = key.substring(0, key.indexOf("."));
                String entityAttrName = key.substring(key.indexOf(".") + 1);
                StringBuffer sql = new StringBuffer();
                String relEntityInfo = addContainer.getRelEntityAttr2AttrMapper().get(key);
                String relEntityName = relEntityInfo.substring(0, relEntityInfo.indexOf("."));
                String relEntityAttrName = relEntityInfo.substring(relEntityInfo.indexOf(".") + 1);
                sql.append("alter table " + backQuote(entityName,backQuote)  + " add constraint FK_" + entityName + "_4_" + entityAttrName
                        + " foreign key ( " + entityAttrName + ") references " + relEntityName +"(" + relEntityAttrName
                        +") on delete restrict on update restrict;");
                tempList.add(sql.toString());
            }
        }

        entityAttrMap = modifyContainer.getEntityAttrMap();
        for (String key : entityAttrMap.keySet()) {
            EntityAttr entityAttr = entityAttrMap.get(key);
            if(entityAttr.getRelHfmdEntityAttrId() != null && entityAttr.getRelHfmdEntityAttrId() > 0
                    && modifyContainer.getEntityAttrChangeTypeMap().containsKey(entityAttr)
                    && modifyContainer.getEntityAttrChangeTypeMap().get(entityAttr).containFk() && containFk) {

                String entityName = key.substring(0, key.indexOf("."));
                String entityAttrName = key.substring(key.indexOf(".") + 1);
                StringBuffer sql = new StringBuffer();
                String relEntityInfo = modifyContainer.getRelEntityAttr2AttrMapper().get(key);
                String relEntityName = relEntityInfo.substring(0, relEntityInfo.indexOf("."));
                String relEntityAttrName = relEntityInfo.substring(relEntityInfo.indexOf(".") + 1);
                sql.append("alter table " + entityName + " add constraint FK_" + entityName + "_4_" + entityAttrName
                        + " foreign key ( " + entityAttrName + ") references " + relEntityName +"(" + relEntityAttrName
                        +") on delete restrict on update restrict;");
                tempList.add(sql.toString());
            }
        }

        Collections.sort(tempList);
        result.addAll(tempList);
        return result;
    }

    private static String backQuote(String entityName, boolean backQuote) {
        return backQuote? ("`" + entityName + "`") : entityName;
    }

    private static String getColumnInfo(EntityAttr entityAttr, boolean backQuote) {
        if(entityAttr !=null) {
            String comment = entityAttr.getHfmdEntityAttrName();
            if(entityAttr.getHfmdEntityAttrName() != null && entityAttr.getHfmdEntityAttrDesc() != null
                    && !entityAttr.getHfmdEntityAttrName().equals(entityAttr.getHfmdEntityAttrDesc())) {
                comment +=( "," +  entityAttr.getHfmdEntityAttrDesc());
            }
            System.out.println("=>" + entityAttr.getHfmdEntityAttrCode() + "|" + entityAttr.getAttrType() + "|" + entityAttr.getSize() + "|" + entityAttr.getIspk() + "|" + entityAttr.getNullable());
            return backQuote(entityAttr.getHfmdEntityAttrCode(),backQuote) + " " + HfmdEntityAttr1AttrTypeEnum.getName(entityAttr.getAttrType())
                    + (StringUtils.isNotBlank(entityAttr.getSize()) ? ("(" + entityAttr.getSize() + ")" ): "")
                    + (entityAttr.getIspk() != null && entityAttr.getIspk() == 1? " primary key auto_increment" : "")
                    + (entityAttr.getNullable() != null && entityAttr.getNullable() == 1? "" : " not null")
                    + (StringUtils.isNotBlank(entityAttr.getHfmdEntityAttrName()) ? " comment '" + comment + "'" : "");
        }
        return null;

    }

    public static HfModelContainer getInstance() {
        return new HfModelContainer();
    }
}

