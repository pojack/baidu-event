package cn.edu.swpu.cins.event.analyse.platform.controller;

import cn.edu.swpu.cins.event.analyse.platform.exception.BaseException;
import cn.edu.swpu.cins.event.analyse.platform.model.view.SpecialEventPage;
import cn.edu.swpu.cins.event.analyse.platform.model.view.ViewObject;
import cn.edu.swpu.cins.event.analyse.platform.service.SpecialEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event/specialEvent")
public class SpecialEventController {

    @Autowired
    private SpecialEventService specialEventService;


    //获取专题事件的集合
    @PostMapping("/{page}")
    public ResponseEntity<?> getTopics(@PathVariable int page
            , @RequestBody SpecialEventPage specialEventPage) {
        try {

            if(specialEventPage.getIds().isEmpty()){
                return new ResponseEntity<Object>("0",HttpStatus.OK);
            }
            ViewObject vo= specialEventService.getSpecialEvent(page,false , specialEventPage.getMore(),specialEventPage.getIds());
            return new ResponseEntity<>(vo, HttpStatus.OK);
        } catch (BaseException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

}
