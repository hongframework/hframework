package org.activiti.rest.editor.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by zhangquanhong on 2017/1/11.
 */
@RestController
public class ModelSaveRestResource implements ModelDataJsonConstants {
    protected static final Logger logger = LoggerFactory.getLogger(ModelSaveRestResource.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;
    /**
     * 重写 {@link ModelSaveRestResource} 的 saveModel方法
     * @param modelId
     * @param name
     * @param description
     * @param json_xml
     * @param svg_xml
     */
    @RequestMapping(value="/model/{modelId}/save", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public void saveModel(@PathVariable String modelId, String name , String description, String json_xml, String svg_xml) {
        try {

            Model model = repositoryService.getModel(modelId);

            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());

            modelJson.put(ModelDataJsonConstants.MODEL_NAME, name);
            modelJson.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            model.setMetaInfo(modelJson.toString());
            model.setName(name);

            repositoryService.saveModel(model);

            repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes());

            InputStream svgStream = new ByteArrayInputStream(svg_xml.getBytes());
            TranscoderInput input = new TranscoderInput(svgStream);

            PNGTranscoder transcoder = new PNGTranscoder();
            // Setup output
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);

            // Do the transformation
            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            repositoryService.addModelEditorSourceExtra(model.getId(), result);
            outStream.close();

//            HfpmDataField_Example example = new HfpmDataField_Example();
//            example.createCriteria().andWorkfowModelIdEqualTo(modelId);
//            List<HfpmDataField> hfpmDataFieldList = hfpmDataFieldSV.getHfpmDataFieldListByExample(example);
//            if(hfpmDataFieldList.size() > 0) {
//                HfpmDataSet hfpmDataSet = hfpmDataSetSV.getHfpmDataSetByPK(hfpmDataFieldList.get(0).getHfpmDataSetId());
//                HfpmProgram hfpmProgram = hfpmProgramSV.getHfpmProgramByPK(hfpmDataSet.getHfpmProgramId());
//
//                String companyCode = "hframe";
//                String programCode = hfpmProgram.getHfpmProgramCode();
//
//
//                final ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(json_xml.getBytes());
//                BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(modelNode);
//                byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
//                String workflowContent = new String(bpmnBytes);
//                String workflowFileName = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
//
//                WebContextHelper contextHelper = new WebContextHelper(companyCode, programCode, null, null);
//                String workflowFilePath = contextHelper.programConfigRootDir + "/" +
//                        contextHelper.programConfigDataSetDir + "/process/" + workflowFileName;
//                FileUtils.writeFile(workflowFilePath, workflowContent);
//            }
        } catch (Exception e) {
            logger.error("Error saving model", e);
            throw new ActivitiException("Error saving model", e);
        }
    }
}
