package com.learning.erp.business.material.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.erp.business.material.entity.Material;
import com.learning.erp.business.material.mapper.MaterialMapper;
import com.learning.erp.common.annotation.SysLog;
import com.learning.erp.common.exception.BusinessException;
import com.learning.erp.common.exception.ErrorCode;
import com.learning.erp.common.result.PageResult;
import com.learning.erp.common.result.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business/material")
public class MaterialController {

    private final MaterialMapper mapper;
    public MaterialController(MaterialMapper mapper) { this.mapper = mapper; }

    @PreAuthorize("hasAnyAuthority('*:*:*', 'mat:material:list')")
    @GetMapping("/page")
    public Result<PageResult<Material>> page(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String keyword) {
        var w = new LambdaQueryWrapper<Material>()
            .and(StringUtils.hasText(keyword), q -> q
                .like(Material::getCode, keyword).or().like(Material::getName, keyword))
            .orderByDesc(Material::getId);
        Page<Material> p = mapper.selectPage(new Page<>(page, size), w);
        return Result.ok(new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    @PreAuthorize("hasAnyAuthority('*:*:*', 'mat:material:list')")
    @GetMapping("/{id}")
    public Result<Material> get(@PathVariable Long id) {
        Material m = mapper.selectById(id);
        if (m == null) throw new BusinessException(ErrorCode.MATERIAL_NOT_FOUND);
        return Result.ok(m);
    }

    @PreAuthorize("hasAnyAuthority('*:*:*', 'mat:material:add')")
    @SysLog(module = "物料", operation = "新增")
    @PostMapping
    public Result<Long> create(@RequestBody Material m) {
        Material exist = mapper.selectOne(new LambdaQueryWrapper<Material>()
            .eq(Material::getCode, m.getCode()));
        if (exist != null) throw new BusinessException(30002, "物料编码已存在");
        if (m.getStatus() == null) m.setStatus(1);
        mapper.insert(m);
        return Result.ok(m.getId());
    }

    @PreAuthorize("hasAnyAuthority('*:*:*', 'mat:material:edit')")
    @SysLog(module = "物料", operation = "修改")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Material m) {
        m.setId(id);
        mapper.updateById(m);
        return Result.ok();
    }

    @PreAuthorize("hasAnyAuthority('*:*:*', 'mat:material:remove')")
    @SysLog(module = "物料", operation = "删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mapper.deleteById(id);
        return Result.ok();
    }
}
