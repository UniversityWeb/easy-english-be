package com.universityweb.drip.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.drip.Drip;
import com.universityweb.drip.dto.DripDTO;
import com.universityweb.drip.dto.DripsOfPrevDTO;

import java.util.List;

public interface DripService extends BaseService<Drip, DripDTO, Long> {
    List<DripDTO> getAllDripsByPrevId(Long prevId);
    List<DripsOfPrevDTO> getAllDripsByCourseId(Long courseId);
    Boolean canLearn(Drip.ESourceType targetType, Long targetId);
}
