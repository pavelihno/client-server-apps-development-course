package com.mirea

import java.nio.file.Paths

import javax.annotation.PostConstruct
import org.springframework.web.multipart.MultipartFile


class FileSystemController {

    String uploadPath

    @PostConstruct
    def init() {
        // def appBasePath = grailsApplication.mainContext.servletContext.getRealPath('/')
        // def storagePath = grailsApplication.config.filesystem.path
        
        uploadPath = grailsApplication.config.filesystem.path
    }

    def uploadFile() {
        def file = request.getMultiFileMap().file.first()

        if (file) {
            
            def fileName = file.originalFilename
            def filePath = "${uploadPath}/${fileName}"
            file.transferTo(new File(filePath))
        
            flash.message = "File uploaded successfully"
        } else {
            flash.message = "Please select a file to upload"
        }
        redirect action: "index"
    }

    def index() {
        def fileList = new File(uploadPath).listFiles()
        [fileList: fileList]
    }

    def downloadFile(String fileName) {
        def filePath = "${uploadPath}/${fileName}"
        def file = new File(filePath)
        if (file.exists()) {
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment; filename=${fileName}")
            response.outputStream << file.bytes
        } else {
            flash.message = "File not found"
            redirect action: "index"
        }
    }
}