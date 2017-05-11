import com.hframework.common.frame.ServiceFactory;
import com.hframework.generator.web.container.HfClassContainer;
import com.hframework.generator.web.container.HfClassContainerUtil;
import com.hframework.generator.web.container.HfModelContainer;
import com.hframework.generator.web.container.HfModelContainerUtil;
import com.hframework.generator.web.sql.SqlGeneratorUtil;
import com.hframework.generator.web.sql.reverse.SQLParseUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangqh6 on 2015/10/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config.xml")
public class SqlGeneratorUtilTest {

    @Autowired
    protected ApplicationContext ctx;
    @Before
    public void init() throws Exception {
        ServiceFactory.initContext(ctx);
    }

    @Test
    public void generator() throws Exception {
        System.out.println(" => " + SqlGeneratorUtil.createSqlFile("hframework", "hframe"));;
    }

    @Test
    public HfClassContainer modelContainer2ClassContainer() throws Exception {
        String filePath = SqlGeneratorUtil.createSqlFile("hframework", "hframe");
        String programCode = "hframe";
        String programeName = "框架";
        String moduleCode = "hframe";
        String moduleName = "框架";
        HfModelContainer targetFileModelContainer = SQLParseUtil.parseModelContainerFromSQLFile(
                filePath, programCode, programeName, moduleCode, moduleName);
        HfClassContainer classContainer = HfClassContainerUtil.getClassInfoContainer(targetFileModelContainer);
        System.out.println(" => " + classContainer);
        return classContainer;
    }


    @Test
    public HfClassContainer readFromClassLoader() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, MalformedURLException {
//        String filePath = System.getProperty("user.dir") + "\\hframe-core\\target\\classes\\";


        String programCode = "hframe";
        String programName = "框架";

        String filePath = "/D:/my_workspace/hframe-trunk" + "\\hframe-core\\target\\classes\\";
        String classPackage = "com.hframe.domain.model.";
        HfClassContainer classContainer = HfClassContainerUtil.fromClassPath(filePath, classPackage, programCode, programName);
        System.out.println(" => " + classContainer);
        return classContainer;
    }

    public void compareClassContainer() throws Exception {
        HfClassContainer originContainer = readFromClassLoader();
        HfClassContainer targetContainer = modelContainer2ClassContainer();
        List<Map<String, String>>[] compare = HfClassContainerUtil.compare(originContainer, targetContainer);
    }


    @Test
    public void compare() throws Exception {
        String rootClassPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.out.println(rootClassPath);
//        String filePath = rootClassPath + "\\reversesql\\sql.sql";
//        String filePath = "/D:/my_workspace/hframe-trunk/hframe-generator/target/classes/reversesql/sql.sql";
        String filePath = "/D:/my_workspace/hframe-trunk/hframe-core/src/main/resources/hframe/sql/hframe.sql";


        String programCode = "hframe";
        String programeName = "框架";
        String moduleCode = "hframe";
        String moduleName = "框架";
        HfModelContainer targetFileModelContainer = SQLParseUtil.parseModelContainerFromSQLFile(
                filePath, programCode, programeName, moduleCode, moduleName);

        filePath = SqlGeneratorUtil.getSqlFilePath("hframework", "hframe");
        HfModelContainer curModelContainer = SQLParseUtil.parseModelContainerFromSQLFile(
                filePath, programCode, programeName, moduleCode, moduleName);

        HfModelContainer[] resultModelContainers =
                HfModelContainerUtil.mergerModelContainer(curModelContainer, targetFileModelContainer);
        List<String> result = HfModelContainerUtil.getSql(resultModelContainers[0], resultModelContainers[1]);

        for (String s : result) {
            System.out.println(s);
        }

        resultModelContainers =
                HfModelContainerUtil.mergerEntityToDataSet(resultModelContainers, curModelContainer);
        System.out.println(resultModelContainers);

//        HfModelService.get().executeModelInsert(resultModelContainers[0]);
//        HfModelService.get().executeModelUpdate(resultModelContainers[1]);
    }
}
