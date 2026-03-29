package net.ooder.skill.doc.collab.controller;

import net.ooder.skill.doc.collab.dto.*;
import net.ooder.skill.doc.collab.service.DocCollabService;
import net.ooder.api.result.ResultModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/doc")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DocCollabController {

    @Autowired
    private DocCollabService docCollabService;

    @PostMapping
    public ResultModel<DocumentDTO> createDocument(@RequestBody DocumentDTO doc) {
        log.info("Creating document: {}", doc.getTitle());
        DocumentDTO created = docCollabService.createDocument(doc);
        return ResultModel.success(created);
    }

    @GetMapping("/{docId}")
    public ResultModel<DocumentDTO> getDocument(@PathVariable String docId) {
        DocumentDTO doc = docCollabService.getDocument(docId);
        return ResultModel.success(doc);
    }

    @PutMapping("/{docId}")
    public ResultModel<DocumentDTO> updateDocument(
            @PathVariable String docId,
            @RequestBody DocumentDTO doc) {
        DocumentDTO updated = docCollabService.updateDocument(docId, doc);
        return ResultModel.success(updated);
    }

    @DeleteMapping("/{docId}")
    public ResultModel<Boolean> deleteDocument(
            @PathVariable String docId,
            @RequestParam(required = false) String platform) {
        boolean result = docCollabService.deleteDocument(docId, platform);
        return ResultModel.success(result);
    }

    @PostMapping("/{docId}/share")
    public ResultModel<DocShareDTO> shareDocument(
            @PathVariable String docId,
            @RequestParam(defaultValue = "VIEW") String permission,
            @RequestParam(required = false) String expireTime) {
        DocShareDTO share = docCollabService.shareDocument(docId, permission, expireTime);
        return ResultModel.success(share);
    }

    @PostMapping("/{docId}/comment")
    public ResultModel<DocCommentDTO> addComment(
            @PathVariable String docId,
            @RequestBody DocCommentDTO comment) {
        DocCommentDTO added = docCollabService.addComment(docId, comment);
        return ResultModel.success(added);
    }

    @GetMapping("/{docId}/comments")
    public ResultModel<List<DocCommentDTO>> getComments(@PathVariable String docId) {
        List<DocCommentDTO> comments = docCollabService.getComments(docId);
        return ResultModel.success(comments);
    }

    @GetMapping("/{docId}/export")
    public void exportDocument(
            @PathVariable String docId,
            @RequestParam(defaultValue = "pdf") String format,
            HttpServletResponse response) {
        try {
            byte[] content = docCollabService.exportDocument(docId, format);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=doc_" + docId + "." + format);
            response.getOutputStream().write(content);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("Export document failed", e);
        }
    }

    @GetMapping("/list")
    public ResultModel<List<DocumentDTO>> listDocuments(
            @RequestParam String userId,
            @RequestParam(required = false) String folderId) {
        List<DocumentDTO> docs = docCollabService.listDocuments(userId, folderId);
        return ResultModel.success(docs);
    }
}
