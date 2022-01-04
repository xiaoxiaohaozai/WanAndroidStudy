package com.hc.wandroidstudy.common.data.network

import com.hc.wandroidstudy.common.data.bean.CommonResponse
import okhttp3.Response
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convertTo
import java.lang.reflect.Type


/**
  * @author ace
  * @createDate 2021/12/21
  * @explain
 *  统一错误处理
 */
@Parser(name = "Response")
open class ResponseParse<T> : TypeParser<T> {

    //以下两个构造方法是必须的
    protected constructor() : super()
    constructor(type: Type) : super(type)

    override fun onParse(response: Response): T {
        val data: CommonResponse<T> = response.convertTo(CommonResponse::class, *types)
        val t = data.data     //获取data字段
        if (data.errorCode != 0 || t == null) { //code不等于200，说明数据不正确，抛出异常
            throw ParseException(data.errorCode.toString(), data.errorMsg, response)
        }
        return t
    }
}