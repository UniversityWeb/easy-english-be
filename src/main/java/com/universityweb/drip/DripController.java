package com.universityweb.drip;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.drip.dto.DripDTO;
import com.universityweb.drip.dto.DripsOfPrevDTO;
import com.universityweb.drip.service.DripService;
import io.swagger.v3.oas.annotations.tags.Tag;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/drips")
@RestController
@Tag(name = "Course Drips")
public class DripController extends BaseController<Drip, DripDTO, Long, DripService> {

    @Autowired
    public DripController(DripService service) {
        super(service);
    }

    @GetMapping("/get-all-by-prev/{prevId}")
    public ResponseEntity<List<DripDTO>> getAllDripsByPrevId(
            @PathVariable Long prevId
    ) {
        log.info("Fetching all drips with prevId: {}", prevId);
        List<DripDTO> dripDTOs = service.getAllDripsByPrevId(prevId);
        log.info("Found {} drips with prevId: {}", dripDTOs.size(), prevId);
        return ResponseEntity.ok(dripDTOs);
    }

    @GetMapping("/get-all-by-course/{courseId}")
    public ResponseEntity<List<DripsOfPrevDTO>> getAllDripsByCourseId(
            @PathVariable Long courseId
    ) {
        log.info("Fetching all drips by courseId: {}", courseId);
        List<DripsOfPrevDTO> dripDTOs = service.getAllDripsByCourseId(courseId);
        log.info("Found {} drips for courseId: {}", dripDTOs.size(), courseId);
        return ResponseEntity.ok(dripDTOs);
    }

    @GetMapping("/can-learn")
    public ResponseEntity<Boolean> canLearn(
            @RequestParam Drip.ESourceType targetType,
            @RequestParam Long targetId
    ) {
        log.info("Checking if targetId: {} of type: {} can be learned", targetId, targetType);
        Boolean canLearn = service.canLearn(targetType, targetId);
        log.info("Result for canLearn: {}", canLearn);
        return ResponseEntity.ok(canLearn);
    }

    @PutMapping("/update-drips/{courseId}")
    public ResponseEntity<List<DripsOfPrevDTO>> updateDrips(
            @PathVariable Long courseId,
            @RequestBody List<DripsOfPrevDTO> dripsUpdateRequest
    ) {
        List<DripsOfPrevDTO> updatedDrips = service.updateDrips(courseId, dripsUpdateRequest);
        return ResponseEntity.ok(updatedDrips);
    }
}
