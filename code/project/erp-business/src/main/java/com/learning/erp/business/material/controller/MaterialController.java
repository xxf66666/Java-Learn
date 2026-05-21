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

/**
 * 物料 CRUD 接口
 *
 * 通过 @PreAuthorize 控制权限：
 *  - '*:*:*' 是超管通配符（admin 用户有）
 *  - 'mat:material:list' 是按钮级权限（普通用户角色绑定的菜单含这个 permission）
 */
@RestController
@RequestMapping("/api/business/material")
public class MaterialController {

    private final MaterialMapper mapper;
    public MaterialController(MaterialMapper mapper) { this.mapper = mapper; }

    /**
     * 分页列表
     * hasAnyAuthority(...): 拥有列表中任一权限即可
     */
    @PreAuthorize("hasAnyAuthority('*:*:*', 'mat:material:list')")
    @GetMapping("/page")
    public Result<PageResult<Material>> page(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String keyword) {
        // 动态条件：keyword 非空才加 LIKE 子句
        var w = new LambdaQueryWrapper<Material>()
            .and(StringUtils.hasText(keyword), q -> q
                .like(Material::getCode, keyword)               // code LIKE %keyword%
                .or()                                              // 或
                .like(Material::getName, keyword))               // name LIKE %keyword%
            .orderByDesc(Material::getId);

        // selectPage 返回 MyBatis-Plus 的 Page；我们封装到 PageResult 返回给前端
        Page<Material> p = mapper.selectPage(new Page<>(page, size), w);
        return Result.ok(new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    /** 查详情 */
    @PreAuthorize("hasAnyAuthority('*:*:*', 'mat:material:list')")
    @GetMapping("/{id}")
    public Result<Material> get(@PathVariable Long id) {
        Material m = mapper.selectById(id);
        if (m == null) throw new BusinessException(ErrorCode.MATERIAL_NOT_FOUND);
        return Result.ok(m);
    }

    /** 新增；标了 @SysLog 会自动写操作日志 */
    @PreAuthorize("hasAnyAuthority('*:*:*', 'mat:material:add')")
    @SysLog(module = "物料", operation = "新增")
    @PostMapping
    public Result<Long> create(@RequestBody Material m) {
        // 物料编码唯一性校验
        Material exist = mapper.selectOne(new LambdaQueryWrapper<Material>()
            .eq(Material::getCode, m.getCode()));
        if (exist != null) throw new BusinessException(30002, "物料编码已存在");

        // null 时给默认值
        if (m.getStatus() == null) m.setStatus(1);
        mapper.insert(m);
        return Result.ok(m.getId());
    }

    /** 修改 */
    @PreAuthorize("hasAnyAuthority('*:*:*', 'mat:material:edit')")
    @SysLog(module = "物料", operation = "修改")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Material m) {
        // 用 PathVariable 的 id 覆盖请求体里的 id，防止伪造
        m.setId(id);
        mapper.updateById(m);
        return Result.ok();
    }

    /** 删除（逻辑删除：表里 deleted 字段置 1） */
    @PreAuthorize("hasAnyAuthority('*:*:*', 'mat:material:remove')")
    @SysLog(module = "物料", operation = "删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mapper.deleteById(id);
        return Result.ok();
    }
}
