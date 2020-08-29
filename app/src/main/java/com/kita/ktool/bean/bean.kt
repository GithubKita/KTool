package com.kita.ktool.bean

/**

 * 作者：PC on 2020/8/29 10:51
 * 邮箱：wang_kita@163.com
 * 实体类
 */

class GoodsBean(
    val goodsId: String,
    val goodsName: String,
    val in_price: Float,
    val out_price: Float,
    val sale_price: Float,
    val thumb_img: String?,
    val img: String?
)