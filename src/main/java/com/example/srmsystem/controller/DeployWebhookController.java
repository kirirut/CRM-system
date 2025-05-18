package com.example.srmsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class DeployWebhookController {

    @PostMapping
    public ResponseEntity<String> handleWebhook() {
        try {
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "deploy.sh");
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return ResponseEntity.ok("Deploy script executed successfully");
            } else {
                return ResponseEntity.status(500)
                        .body("Deploy script failed with exit code " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error executing deploy script");
        }
    }
}
