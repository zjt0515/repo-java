package com.zjt.codingsandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.zjt.codingsandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProcessUtils {

    /**
     * 执行进程，获取错误码/输出流/执行时间
     * @param process 进程
     * @param cmdName 命令名称
     * @return ExecuteMessage
     */
    public static ExecuteMessage runProcess(Process process, String cmdName){
        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // 获取命令错误码
            int exitValue = process.waitFor();
            executeMessage.setExitValue(exitValue);

            // 命令执行成功
            if(exitValue == 0){
                System.out.println("runProcess: " + cmdName + "success");
                // 获取输出流，同时拼接成string
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                //String outputLine;
                //StringBuilder outputStringBuilder = new StringBuilder();
                //while((outputLine = bufferedReader.readLine()) != null)
                //{
                //    System.out.println(outputLine);
                //    outputStringBuilder.append(outputLine);
                //}
                //executeMessage.setMessage(outputStringBuilder.toString());

                List<String> outputStrList = new ArrayList<>();
                // 逐行读取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(compileOutputLine);
                }
                executeMessage.setMessage(StringUtils.join(outputStrList, "\n"));
            }else {
                System.out.println(cmdName + " error!: " + exitValue);
                // 获取输出流
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String outputLine;
                StringBuilder outputStringBuilder = new StringBuilder();

                while((outputLine = bufferedReader.readLine()) != null)
                {
                    System.out.println("runProcess输出流: " + outputLine);
                    outputStringBuilder.append(outputLine);
                }
                executeMessage.setErrMessage(outputStringBuilder.toString());
            }
            // Set Time
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
        }catch (Exception e){
            e.printStackTrace();
        }
        return executeMessage;
    }

    public static ExecuteMessage runProcessWithSin(Process process, String opName,String args){
        log.info("runProcessWithSin: " + opName +  "参数: " +args);
        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            // 等待用户输入
            OutputStream outputStream = process.getOutputStream();

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

            String[] s = args.split(" ");
            outputStreamWriter.write(StrUtil.join("\n", s) + "\n");
            // 回车 -> 发送输入
            outputStreamWriter.flush();

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            //  获取正常输出
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            List<String> outputStrList = new ArrayList<>();

            // 逐行读取
            String outputLine;
            while ((outputLine = bufferedReader.readLine()) != null) {
                System.out.println("runProcess输出流: " + outputLine);

                outputStrList.add(outputLine);
            }
            executeMessage.setMessage(StringUtils.join(outputStrList, "\n"));

            // 等待程序执行，获取错误码
            int exitValue = process.waitFor();
            executeMessage.setExitValue(exitValue);

            // Set Time
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());

            // Clean
            outputStreamWriter.close();
            outputStream.close();
            inputStream.close();
            process.destroy();
        }catch (Exception e){
            e.printStackTrace();
        }

        return executeMessage;
    }
}
