package com.learning.erp.business.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.erp.business.stock.entity.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface StockMapper extends BaseMapper<Stock> {

    @Select("SELECT * FROM wms_stock WHERE material_id = #{materialId} AND warehouse_id = #{warehouseId}")
    Stock selectByMatAndWh(@Param("materialId") Long materialId, @Param("warehouseId") Long warehouseId);
}
