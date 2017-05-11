import com.hframework.beans.class0.Table;
import com.hframework.generator.web.BaseGeneratorUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by zhangqh6 on 2015/9/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/spring-config.xml")
public class CommonGeneratorTest {


    @Test
    public void serviceGenerate() throws Exception {
        Table table = new Table();
        table.setTableName("hfmd_entity_join_rule");
        table.setTableDesc("实体属性连带规则");
        BaseGeneratorUtil.serviceGenerate("", "hframe", "hframe", table);
    }

    @Test
    public void controllerGenerate() throws Exception {
        Table table = new Table();
        table.setTableName("hfmd_entity_join_rule");
        table.setTableDesc("实体属性连带规则");
//        table.setParentId("parent_hfsec_menu_id");
        BaseGeneratorUtil.controllerGenerate("", "hframe", "hframe", table);
    }

    @Test
    public void generator() throws Exception {
        BaseGeneratorUtil.generator();
    }
}
