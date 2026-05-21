package com.learning.erp.system.dict.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.erp.common.result.Result;
import com.learning.erp.system.dict.entity.SysDictItem;
import com.learning.erp.system.dict.mapper.SysDictItemMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dict")
public class DictController {

    private final SysDictItemMapper mapper;

    public DictController(SysDictItemMapper mapper) { this.mapper = mapper; }

    @GetMapping("/{code}")
    public Result<List<SysDictItem>> listByCode(@PathVariable String code) {
        var list = mapper.selectList(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictCode, code)
                .eq(SysDictItem::getStatus, 1)
                .orderByAsc(SysDictItem::getSort));
        return Result.ok(list);
    }
}
