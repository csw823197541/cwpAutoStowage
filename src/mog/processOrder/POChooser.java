package mog.processOrder;

import mog.entity.*;
import mog.processType.WorkType;

import java.util.Map;
import java.util.Set;

/**
 * Created by csw on 2016/8/2 9:31.
 * Explain: 编写作业序列
 */
public class POChooser {

    private int seq;//作业顺序

    public POChooser() {
        this.seq = 1;
    }

    //判断MOSlotBlock中所有箱子是否都被编序
    public boolean isAllMOSlotStackEmpty(MOSlotBlock moSlotBlock) {
        boolean result = false;

        for (MOSlotPosition moSlotPosition : moSlotBlock.getSlotPositions()) {
            MOSlot moSlot = moSlotBlock.getMOSlot(moSlotPosition);
            if (moSlot != null) {
                if (moSlot.getMoveType() != null) {
                    if (moSlot.getMoveOrderSeq() == -1) {
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    //判断栈顶有没有该作业工艺的slot
    public boolean isContinueSameTPTop(WorkType wt, MOSlotBlock moSlotBlock) {
        boolean result = false;

        Map<Integer, MOSlotStack> bay01 = moSlotBlock.getBay01();
        Map<Integer, MOSlotStack> bay03 = moSlotBlock.getBay03();

        for (MOSlotStack moSlotStack : bay01.values()) {
            MOSlot moSlot = moSlotStack.getTopMOSlot();
            if (moSlot != null) {
                if (moSlot.getMoveOrderSeq() == -1) {//没有编过MoveOrder
                    MOContainer moContainer = moSlot.getMoContainer();
                    Set<MOSlotPosition> moSlotPositionSet = moSlot.getMoSlotPositionSet();
                    if (moContainer != null && !moSlotPositionSet.isEmpty()) {
                        if (moContainer.size.startsWith(wt.size) && wt.n == moSlotPositionSet.size()) {
                            result = true;
                        }
                    } else {
                        moSlotStack.topTierNoDownBy2();
                    }
                }
            }
        }

        for (MOSlotStack moSlotStack : bay03.values()) {
            MOSlot moSlot = moSlotStack.getTopMOSlot();
            if (moSlot != null) {
                if (moSlot.getMoveOrderSeq() == -1) {//没有编过MoveOrder
                    MOContainer moContainer = moSlot.getMoContainer();
                    Set<MOSlotPosition> moSlotPositionSet = moSlot.getMoSlotPositionSet();
                    if (moContainer != null && !moSlotPositionSet.isEmpty()) {
                        if (moContainer.size.startsWith(wt.size) && wt.n == moSlotPositionSet.size()) {
                            result = true;
                        }
                    } else {
                        moSlotStack.topTierNoDownBy2();
                    }
                }
            }
        }

        return result;
    }

    //判断栈底有没有该作业工艺的slot
    public boolean isContinueSameTPBottom(WorkType wt, MOSlotBlock moSlotBlock) {
        boolean result = false;

        Map<Integer, MOSlotStack> bay01 = moSlotBlock.getBay01();
        Map<Integer, MOSlotStack> bay03 = moSlotBlock.getBay03();

        for (MOSlotStack moSlotStack : bay01.values()) {
            MOSlot moSlot = moSlotStack.getBottomMOSlot();
            if (moSlot != null) {
                if (moSlot.getMoveOrderSeq() == -1) {//没有编过MoveOrder
                    MOContainer moContainer = moSlot.getMoContainer();
                    Set<MOSlotPosition> moSlotPositionSet = moSlot.getMoSlotPositionSet();
                    if (moContainer != null && !moSlotPositionSet.isEmpty()) {
                        if (moContainer.size.startsWith(wt.size) && wt.n == moSlotPositionSet.size()) {
                            result = true;
                        }
                    } else {
                        moSlotStack.bottomTierNoUpBy2();
                    }
                }
            }
        }

        for (MOSlotStack moSlotStack : bay03.values()) {
            MOSlot moSlot = moSlotStack.getBottomMOSlot();
            if (moSlot != null) {
                if (moSlot.getMoveOrderSeq() == -1) {//没有编过MoveOrder
                    MOContainer moContainer = moSlot.getMoContainer();
                    Set<MOSlotPosition> moSlotPositionSet = moSlot.getMoSlotPositionSet();
                    if (moContainer != null && !moSlotPositionSet.isEmpty()) {
                        if (moContainer.size.startsWith(wt.size) && wt.n == moSlotPositionSet.size()) {
                            result = true;
                        }
                    } else {
                        moSlotStack.bottomTierNoUpBy2();
                    }
                }
            }
        }

        return result;
    }

    //处理卸船编MoveOrder的过程
    public MOSlotBlock processD(WorkType wt, Map<Integer, MOSlotStack> bay, MOSlotBlock moSlotBlock) {

        //按从偶数排开始遍历栈顶
//        //取所有栈顶，选择层数最小的开始编序
//        int tierMin = 1000;
//        int rowStart = 1000;
//        for (int j = 0; j < moSlotBlock.getRowSeqList().size(); j++) {
//            int row = moSlotBlock.getRowSeqList().get(j);
//            MOSlotStack moSlotStack = bay.get(row);
//            if(moSlotStack != null) {
//                MOSlot moSlotTop = moSlotStack.getTopMOSlot();
//                if(moSlotTop != null) {
//                    if (moSlotTop.getMoveOrderSeq() == -1) {
//                        MOContainer moContainer = moSlotTop.getMoContainer();
//                        Set<MOSlotPosition> moSlotPositionSet = moSlotTop.getMoSlotPositionSet();
//                        if (moContainer != null && !moSlotPositionSet.isEmpty()) {
//                            if (moContainer.size.startsWith(wt.size) && wt.n == moSlotPositionSet.size()) {
//                                if (moSlotTop.getMoSlotPosition().getTierInt() < tierMin) {
//                                    tierMin = moSlotTop.getMoSlotPosition().getTierInt();
//                                    rowStart = row;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        MOSlotStack moSlotStack = bay.get(rowStart);
//        if (moSlotStack != null) {
//            MOSlot moSlotTop = moSlotStack.getTopMOSlot();
//            if (moSlotTop != null) {
//                if (moSlotTop.getMoveOrderSeq() == -1) {
//                    MOContainer moContainer = moSlotTop.getMoContainer();
//                    Set<MOSlotPosition> moSlotPositionSet = moSlotTop.getMoSlotPositionSet();
//                    if (moContainer != null && !moSlotPositionSet.isEmpty()) {
//                        if (moContainer.size.startsWith(wt.size) && wt.n == moSlotPositionSet.size()) {
//                            //从moSlotPositionSet中先获取slot，然后编序编MoveOrder
//                            for (MOSlotPosition moSlotPosition : moSlotPositionSet) {
//                                MOSlot moSlot = moSlotBlock.getMOSlot(moSlotPosition);
//                                moSlot.setMoveOrderSeq(seq);
//                            }
//                            seq++;
//                            //编完序后，栈顶标记下移
//                            moSlotStack.topTierNoDownBy2();
//                        }
//                    }
//                } else {//已经编了序号，下移栈顶
//                    moSlotStack.topTierNoDownBy2();
//                }
//            }
//        }


        for (int j = 0; j < moSlotBlock.getRowSeqList().size(); j++) {
            int row = moSlotBlock.getRowSeqList().get(j);
            MOSlotStack moSlotStack = bay.get(row);
            if (moSlotStack != null) {
                MOSlot moSlotTop = moSlotStack.getTopMOSlot();
                if (moSlotTop != null) {
                    if (moSlotTop.getMoveOrderSeq() == -1) {
                        MOContainer moContainer = moSlotTop.getMoContainer();
                        Set<MOSlotPosition> moSlotPositionSet = moSlotTop.getMoSlotPositionSet();
                        if (moContainer != null && !moSlotPositionSet.isEmpty()) {
                            if (moContainer.size.startsWith(wt.size) && wt.n == moSlotPositionSet.size()) {
                                //从moSlotPositionSet中先获取slot，然后编序编MoveOrder
                                for (MOSlotPosition moSlotPosition : moSlotPositionSet) {
                                    MOSlot moSlot = moSlotBlock.getMOSlot(moSlotPosition);
                                    moSlot.setMoveOrderSeq(seq);
                                }
                                seq++;
                                //编完序后，栈顶标记下移
                                moSlotStack.topTierNoDownBy2();
                            }
                        }
                    } else {//已经编了序号，下移栈顶
                        moSlotStack.topTierNoDownBy2();
                    }
                }
            }
        }

        return moSlotBlock;
    }

    //处理装船编moveOrder的过程
    private MOSlotBlock processL(WorkType wt, Map<Integer, MOSlotStack> bay, MOSlotBlock moSlotBlock) {

        //按从偶数排开始遍历栈底
        for (int j = 0; j < moSlotBlock.getRowSeqList().size(); j++) {
            int row = moSlotBlock.getRowSeqList().get(j);
            MOSlotStack moSlotStack = bay.get(row);
            if (moSlotStack != null) {
                MOSlot moSlotBottom = moSlotStack.getBottomMOSlot();
                if (moSlotBottom != null) {
                    if (moSlotBottom.getMoveOrderSeq() == -1) {
                        MOContainer moContainer = moSlotBottom.getMoContainer();
                        Set<MOSlotPosition> moSlotPositionSet = moSlotBottom.getMoSlotPositionSet();
                        if (moContainer != null && !moSlotPositionSet.isEmpty()) {
                            if (moContainer.size.startsWith(wt.size) && wt.n == moSlotPositionSet.size()) {
                                //从moSlotPositionSet中先获取slot，然后编序编MoveOrder
                                for (MOSlotPosition moSlotPosition : moSlotPositionSet) {
                                    MOSlot moSlot = moSlotBlock.getMOSlot(moSlotPosition);
                                    moSlot.setMoveOrderSeq(seq);
                                }
                                seq++;
                                //编完序后，栈底标记上移
                                moSlotStack.bottomTierNoUpBy2();
                            }
                        }
                    } else {//已经编了序号，栈底标记上移
                        moSlotStack.bottomTierNoUpBy2();
                    }
                }
            }
        }

        return moSlotBlock;
    }

    public MOSlotBlock processOrderAD(MOSlotBlock moSlotBlock) {

        Map<Integer, MOSlotStack> bay01 = moSlotBlock.getBay01();
        Map<Integer, MOSlotStack> bay03 = moSlotBlock.getBay03();

        WorkType[] workTypes = new WorkType[]{new WorkType(1, "2"), new WorkType(1, "4"),
                new WorkType(2, "4"), new WorkType(2, "2")};

        while (isAllMOSlotStackEmpty(moSlotBlock)) {
            //对栈顶元素进行编序
            int i = 0;
            while (i < workTypes.length) {
                WorkType wt = workTypes[i];
                while (isContinueSameTPTop(wt, moSlotBlock)) {
                    //先从01贝开始
                    this.processD(wt, bay01, moSlotBlock);
                    //再从03贝开始
                    this.processD(wt, bay03, moSlotBlock);
                }
                boolean isCycle = false;
                for (int j = 0; j < i; j++) {
                    WorkType wt1 = workTypes[j];
                    if (isContinueSameTPTop(wt1, moSlotBlock)) {
                        isCycle = true;
                    }
                }
                if(isCycle) {
                    i = 0;
                } else {
                    i++;
                }
            }
        }

        return moSlotBlock;
    }

    public MOSlotBlock processOrderBD(MOSlotBlock moSlotBlock) {

        return moSlotBlock;
    }

    public MOSlotBlock processOrderBL(MOSlotBlock moSlotBlock) {

        Map<Integer, MOSlotStack> bay01 = moSlotBlock.getBay01();
        Map<Integer, MOSlotStack> bay03 = moSlotBlock.getBay03();

        WorkType[] workTypes = new WorkType[]{new WorkType(1, "2"), new WorkType(2, "2"),
                new WorkType(1, "4"), new WorkType(2, "4")};

        while (isAllMOSlotStackEmpty(moSlotBlock)) {
            int i = 0;
            while (i < workTypes.length) {
                WorkType wt = workTypes[i];
                while (isContinueSameTPBottom(wt, moSlotBlock)) {
                    //先从01贝开始
                    this.processL(wt, bay01, moSlotBlock);
                    //再从03贝开始
                    this.processL(wt, bay03, moSlotBlock);
                }
                boolean isCycle = false;
                for (int j = 0; j < i; j++) {
                    WorkType wt1 = workTypes[j];
                    if (isContinueSameTPBottom(wt1, moSlotBlock)) {
                        isCycle = true;
                    }
                }
                if (isCycle) {
                    i = 0;
                } else {
                    i++;
                }
            }
        }

        return moSlotBlock;
    }

    public MOSlotBlock processOrderAL(MOSlotBlock moSlotBlock) {

        return moSlotBlock;
    }
}
