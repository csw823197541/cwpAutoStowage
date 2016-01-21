package importDataInfo;

/**
 * Created by leko on 2016/1/21.
 */
public class GroupInfo {

    public static String groupID;   //属性组
    public static String port;      //港口
    public static String type;      //箱型
    public static String size;      //尺寸

    public static String getGroupID() {
        return groupID;
    }

    public static void setGroupID(String groupID) {
        GroupInfo.groupID = groupID;
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        GroupInfo.port = port;
    }

    public static String getType() {
        return type;
    }

    public static void setType(String type) {
        GroupInfo.type = type;
    }

    public static String getSize() {
        return size;
    }

    public static void setSize(String size) {
        GroupInfo.size = size;
    }
}
