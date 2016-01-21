package importData;
import importDataInfo.VesselStructureInfo;
import utils.FileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by csw on 2016/1/21.
 */
public class Test {
    public static void main(String[] args) {

        String str = FileUtil.readFileToString(new File("E:/TestData/shipstructure.json")).toString();
        List<VesselStructureInfo> testList = VesselStructureInfoProcess.getVesselStructureInfo(str);
        System.out.println(testList.get(0).getVBYBAYID());
    }
}
