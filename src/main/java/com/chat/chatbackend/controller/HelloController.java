package com.chat.chatbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello-kartik")
    public String sayHello() {
        return """
                <html>
                    <head>
                        <title>Welcome</title>
                        <style>
                            body {
                                background-color: #f0f4f8;
                                font-family: Arial, sans-serif;
                                display: flex;
                                justify-content: center;
                                align-items: center;
                                height: 100vh;
                                margin: 0;
                            }
                            .card {
                                background: white;
                                padding: 40px;
                                border-radius: 10px;
                                box-shadow: 0 4px 8px rgba(0,0,0,0.2);
                                text-align: center;
                            }
                            h1 {
                                color: #333;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="card">
                            <h1>Hi Kartik Ringola 👋</h1>
                            <p>Welcome to your chat backend!</p>
                        </div>
                    </body>
                </html>
                """;
    }
}
