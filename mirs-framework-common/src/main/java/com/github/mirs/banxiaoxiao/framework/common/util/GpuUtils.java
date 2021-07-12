package com.github.mirs.banxiaoxiao.framework.common.util;

import com.github.mirs.banxiaoxiao.framework.common.shell.CmdExecResult;
import com.github.mirs.banxiaoxiao.framework.common.shell.CmdLineExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author bc
 */
@Slf4j
public class GpuUtils {

    /**
     * 获取GPU 个数
     *
     * @return 如果返回0则说明获取失败
     */
    public static int getGpuCount() {
        int gpuCount = 0;

        try {
            String[] command = {"/usr/bin/nvidia-smi", "-L"};
            CmdExecResult cmdExecResult = CmdLineExecutor.execute(command);
            List<String> list = cmdExecResult.getLines();
            if (CollectionUtils.isEmpty(list)) {
                log.error("getGpuCount list is null");
                return gpuCount;
            }

            gpuCount = list.size();
        } catch (Exception ex) {
            log.error("getGpuCount error", ex);
        }

        return gpuCount;
    }

    public static int GetLeastUsedGpuId(Integer gpuMemory) {
        int gpuId = -1;
        int freeGpuMax = 0;
        try {
            String[] command = {"/usr/bin/nvidia-smi", "--query-gpu=memory.free", "--format=csv"};
            CmdExecResult cmdExecResult = CmdLineExecutor.execute(command);
            List<String> list = cmdExecResult.getLines();
            if (!CollectionUtils.isEmpty(list) && list.size() > 1) {
                for (int i = 1; i < list.size(); i++) {
                    Integer freeGpu = Integer.valueOf(list.get(i).split(" ")[0]);
                    if (freeGpuMax < Integer.valueOf(freeGpu)) {
                        freeGpuMax = freeGpu;
                        gpuId = i - 1;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Get the least used GPU");
        }

        if (freeGpuMax < gpuMemory) {
            log.error("memory.free < {}MiB,Inability to handle new tasks", gpuMemory);
            return gpuId;
        }
        log.info("+++++++gpuId：{}", gpuId);
        return gpuId;
    }

    public static Boolean GetLeastUsedGpuId() {
        int gpuID = 0;
        int freeGpuMax = 0;
        boolean canUse = false;
        try {
            String[] command = {"/usr/bin/nvidia-smi", "--query-gpu=memory.free", "--format=csv"};
            CmdExecResult cmdExecResult = CmdLineExecutor.execute(command);
            List<String> list = cmdExecResult.getLines();
            if (!CollectionUtils.isEmpty(list) && list.size() > 1) {
                for (int i = 1; i < list.size(); i++) {
                    Integer freeGpu = Integer.valueOf(list.get(i).split(" ")[0]);
                    if (freeGpuMax < freeGpu) {
                        freeGpuMax = freeGpu;
                        gpuID = i - 1;
                    }
                }
                log.info("gpuID = {} memory.free  = {}", gpuID, freeGpuMax);
                if (freeGpuMax > 1300) {
                    canUse = true;
                }
            }
        } catch (Exception e) {
            log.error("Get the least used GPU");
        }
        log.info("+++++++gupID：{}", gpuID);
        return canUse;
    }

    public static Integer getUserGpuId(int type) {
        int gpuID = 0;
        int freeGpuMax = 0;
        try {
            String[] command = {"/usr/bin/nvidia-smi", "--query-gpu=memory.free", "--format=csv"};
            CmdExecResult cmdExecResult = CmdLineExecutor.execute(command);
            List<String> list = cmdExecResult.getLines();
            if (!CollectionUtils.isEmpty(list) && list.size() > 1) {
                if (type == 1) {
                    boolean canUse = true;
                    for (int i = 1; i < list.size(); i++) {
                        Integer freeGpu = Integer.valueOf(list.get(i).split(" ")[0]);
                        if (freeGpuMax < freeGpu) {
                            freeGpuMax = freeGpu;
                            gpuID = i - 1;
                        }
                        if (freeGpuMax < 1300) {
                            canUse = false;
                            log.info(
                                    "gpuID = {} memory.free < 1300MiB,Inability to initialize faceEngineForBatch",
                                    gpuID);
                        }
                    }
                    log.info("+++++++batch gupID：{}", canUse ? -1 : gpuID);
                    return canUse ? -1 : gpuID;
                } else {
                    for (int i = 1; i < list.size(); i++) {
                        Integer freeGpu = Integer.valueOf(list.get(i).split(" ")[0]);
                        if (freeGpuMax < freeGpu) {
                            freeGpuMax = freeGpu;
                            gpuID = i - 1;
                        }
                    }
                    if (freeGpuMax < 800) {
                        log.info("gpuID = {} memory.free < 800MiB,Inability to initialize faceEngineForNormal",
                                gpuID);
                    }
                    log.info("+++++++gpuID：{}", gpuID);
                    return gpuID;
                }
            }
        } catch (Exception e) {
            log.error("Get the least used GPU");
        }
        log.info("+++++++gupID：{}", gpuID);
        return gpuID;
    }

}
