package com.richard.demo.entity;

import java.io.File;
import java.util.List;

import org.springframework.context.ApplicationEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author richard.xu03@sap.com
 * @version v 0.1 2021/5/28 3:13 PM richard.xu Exp $
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class FileDeletionEvent extends ApplicationEvent {

    private List<File> files;

    public FileDeletionEvent(Object source, List<File> files) {
        super(source);
        this.files = files;
    }
}
