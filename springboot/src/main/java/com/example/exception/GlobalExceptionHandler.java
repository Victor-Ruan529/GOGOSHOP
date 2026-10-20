package com.example.exception;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理：业务异常返回具体提示，未知异常只记录日志，不把堆栈暴露给前端
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public Result customError(CustomException e) {
        return Result.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler({BindException.class, HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public Result paramError(Exception e) {
        log.debug("参数错误: {}", e.getMessage());
        return Result.error(ResultCodeEnum.PARAM_ERROR);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result methodError(HttpRequestMethodNotSupportedException e) {
        return Result.error("405", "不支持的请求方法");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result uploadSizeError(MaxUploadSizeExceededException e) {
        return Result.error(ResultCodeEnum.FILE_SIZE_ERROR);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result duplicateError(DuplicateKeyException e) {
        return Result.error(ResultCodeEnum.PARAM_ERROR.code, "数据已存在");
    }

    @ExceptionHandler(Exception.class)
    public Result error(HttpServletRequest request, Exception e) {
        log.error("请求 {} {} 出现异常", request.getMethod(), request.getRequestURI(), e);
        return Result.error();
    }
}
