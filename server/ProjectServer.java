import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;

public class ProjectServer {
    public static void main(String[] args) throws Exception {
        int port = 5000;
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/", new ProjectHandler());
        server.setExecutor(null);
        System.out.println("Gamekt Pro project server running on port " + port);
        server.start();
    }

    static class ProjectHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = "<!DOCTYPE html>\n" +
                "<html lang=\"pt-BR\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Gamekt Pro - Dragon Core Interactive</title>\n" +
                "  <style>\n" +
                "    * { box-sizing: border-box; margin: 0; padding: 0; }\n" +
                "    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; background: #0a0000; color: #e6d0d0; min-height: 100vh; overflow-x: hidden; }\n" +
                "    .bg { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: radial-gradient(ellipse at 70% 60%, #3a000066 0%, transparent 70%), radial-gradient(ellipse at 30% 20%, #1a000044 0%, transparent 60%); pointer-events: none; }\n" +
                "    .dragon-wrap { position: fixed; right: -20px; bottom: -20px; width: 380px; height: 380px; opacity: 0.22; animation: breathe 4s ease-in-out infinite; pointer-events: none; }\n" +
                "    @keyframes breathe { 0%,100%{transform:scale(1) translateY(0)} 50%{transform:scale(1.03) translateY(-8px)} }\n" +
                "    @keyframes fire { 0%,100%{opacity:0.6;transform:scaleX(1)} 50%{opacity:1;transform:scaleX(1.15)} }\n" +
                "    .container { position: relative; z-index: 1; max-width: 860px; margin: 0 auto; padding: 28px 20px 60px; }\n" +
                "    .header { display: flex; align-items: center; gap: 18px; margin-bottom: 32px; padding-bottom: 24px; border-bottom: 1px solid #3a0000; }\n" +
                "    .logo { width: 72px; height: 72px; background: radial-gradient(circle, #c62828, #4a0000); border-radius: 18px; display: flex; align-items: center; justify-content: center; font-size: 36px; flex-shrink: 0; box-shadow: 0 0 30px #c6282844; animation: glowPulse 3s ease-in-out infinite; }\n" +
                "    @keyframes glowPulse { 0%,100%{box-shadow:0 0 20px #c6282844} 50%{box-shadow:0 0 40px #c62828aa} }\n" +
                "    .app-title { font-size: 2.2rem; font-weight: 800; color: #fff; letter-spacing: -0.5px; }\n" +
                "    .app-subtitle { color: #c62828; font-size: 0.95rem; font-weight: 600; margin-top: 2px; }\n" +
                "    .app-desc { color: #9a7070; font-size: 0.9rem; margin-top: 4px; }\n" +
                "    .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 16px; margin-bottom: 24px; }\n" +
                "    .card { background: #140000cc; border: 1px solid #3a0a0a; border-radius: 14px; padding: 20px; transition: border-color 0.2s; }\n" +
                "    .card:hover { border-color: #8b0000; }\n" +
                "    .card-title { font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.12em; color: #c62828; font-weight: 700; margin-bottom: 12px; display: flex; align-items: center; gap: 6px; }\n" +
                "    .card-title span { font-size: 14px; }\n" +
                "    .tag-row { display: flex; flex-wrap: wrap; gap: 7px; }\n" +
                "    .tag { background: #200000; border: 1px solid #3a0a0a; border-radius: 6px; padding: 4px 11px; font-size: 12px; color: #c9b0b0; }\n" +
                "    .tag.hot { border-color: #8b0000; color: #ff6b6b; }\n" +
                "    .module { background: #140000cc; border: 1px solid #3a0a0a; border-radius: 14px; padding: 18px 20px; margin-bottom: 12px; display: flex; align-items: flex-start; gap: 14px; }\n" +
                "    .module-icon { width: 40px; height: 40px; background: linear-gradient(135deg, #8b0000, #2a0000); border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 18px; flex-shrink: 0; }\n" +
                "    .module-body h3 { color: #fff; font-size: 15px; font-weight: 700; margin-bottom: 3px; }\n" +
                "    .module-body p { color: #9a7070; font-size: 13px; line-height: 1.5; }\n" +
                "    .badge { display: inline-block; padding: 3px 10px; border-radius: 20px; font-size: 11px; font-weight: 700; margin-left: 8px; }\n" +
                "    .badge.new { background: #c62828; color: white; }\n" +
                "    .step-list { list-style: none; }\n" +
                "    .step-list li { padding: 8px 0; border-bottom: 1px solid #200000; color: #c9b0b0; font-size: 14px; display: flex; gap: 10px; }\n" +
                "    .step-list li:last-child { border-bottom: none; }\n" +
                "    .num { background: #c62828; color: white; border-radius: 50%; width: 22px; height: 22px; display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 800; flex-shrink: 0; margin-top: 1px; }\n" +
                "    .links { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 24px; }\n" +
                "    .link-btn { display: inline-flex; align-items: center; gap: 6px; background: #200000; border: 1px solid #3a0a0a; color: #ef9a9a; text-decoration: none; border-radius: 8px; padding: 8px 16px; font-size: 13px; transition: all 0.2s; }\n" +
                "    .link-btn:hover { background: #3a0a0a; border-color: #8b0000; }\n" +
                "    .link-btn.primary { background: #8b0000; border-color: #c62828; color: white; }\n" +
                "    .link-btn.primary:hover { background: #c62828; }\n" +
                "    h2 { color: #fff; font-size: 1.1rem; font-weight: 700; margin-bottom: 14px; display: flex; align-items: center; gap: 8px; }\n" +
                "    h2::before { content: ''; display: block; width: 4px; height: 18px; background: #c62828; border-radius: 2px; }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"bg\"></div>\n" +
                "  <div class=\"container\">\n" +
                "    <div class=\"header\">\n" +
                "      <div class=\"logo\">🐉</div>\n" +
                "      <div>\n" +
                "        <div class=\"app-title\">Gamekt Pro</div>\n" +
                "        <div class=\"app-subtitle\">Dragon Core Interactive</div>\n" +
                "        <div class=\"app-desc\">Jogue seus jogos do Steam, Epic e GOG diretamente no Android — com saves na nuvem.</div>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <div class=\"grid\">\n" +
                "      <div class=\"card\">\n" +
                "        <div class=\"card-title\"><span>⚙️</span> Stack Técnico</div>\n" +
                "        <div class=\"tag-row\">\n" +
                "          <span class=\"tag hot\">Kotlin</span><span class=\"tag hot\">Jetpack Compose</span>\n" +
                "          <span class=\"tag\">C/C++ NDK</span><span class=\"tag\">Wine</span>\n" +
                "          <span class=\"tag\">Box86/Box64</span><span class=\"tag\">DXVK</span>\n" +
                "          <span class=\"tag\">Gradle 8.x</span><span class=\"tag\">Steam API</span>\n" +
                "          <span class=\"tag\">GPL 3.0</span>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "      <div class=\"card\">\n" +
                "        <div class=\"card-title\"><span>📱</span> Dispositivo Alvo</div>\n" +
                "        <div class=\"tag-row\">\n" +
                "          <span class=\"tag hot\">Moto G35</span>\n" +
                "          <span class=\"tag\">Unisoc T760</span>\n" +
                "          <span class=\"tag\">Android 14</span>\n" +
                "          <span class=\"tag\">6 GB RAM</span>\n" +
                "          <span class=\"tag\">IMG BXE-4-32 GPU</span>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <h2>Novos Módulos</h2>\n" +
                "    <div class=\"module\"><div class=\"module-icon\">🐉</div><div class=\"module-body\"><h3>Fundo Animado com Dragão <span class=\"badge new\">NOVO</span></h3><p>Dragão vermelho animado com respiração, fogo e rastreamento de olhar via toque. Renderizado em tempo real com Canvas/OpenGL ES.</p></div></div>\n" +
                "    <div class=\"module\"><div class=\"module-icon\">🔑</div><div class=\"module-body\"><h3>Painel ADM <span class=\"badge new\">NOVO</span></h3><p>Acesso administrativo protegido por senha. Gerencie estoque, jogos e sessões diretamente no app.</p></div></div>\n" +
                "    <div class=\"module\"><div class=\"module-icon\">🛒</div><div class=\"module-body\"><h3>Loja Mercado Livre <span class=\"badge new\">NOVO</span></h3><p>Marketplace integrado para acessórios de jogos. Acesso via botão ADM ou pela biblioteca.</p></div></div>\n" +
                "    <div class=\"module\"><div class=\"module-icon\">🗣️</div><div class=\"module-body\"><h3>Tradução IA + Voz <span class=\"badge new\">NOVO</span></h3><p>Tradução universal por overlay e leitura de textos em voz alta (TTS) para acessibilidade total.</p></div></div>\n" +
                "    <div class=\"module\"><div class=\"module-icon\">🌡️</div><div class=\"module-body\"><h3>Controle Térmico + Otimização <span class=\"badge new\">NOVO</span></h3><p>Monitoramento de temperatura. Acima de 42°C, reduz automaticamente o consumo. Perfis otimizados para RPG e jogos pesados no Unisoc T760.</p></div></div>\n" +
                "\n" +
                "    <br>\n" +
                "    <h2>Como Usar</h2>\n" +
                "    <div class=\"card\">\n" +
                "      <ol class=\"step-list\">\n" +
                "        <li><span class=\"num\">1</span>Baixe o APK mais recente na página de releases</li>\n" +
                "        <li><span class=\"num\">2</span>Instale o APK no seu dispositivo Android</li>\n" +
                "        <li><span class=\"num\">3</span>Entre na sua conta Steam</li>\n" +
                "        <li><span class=\"num\">4</span>Instale o jogo desejado</li>\n" +
                "        <li><span class=\"num\">5</span>Clique em Jogar e aproveite!</li>\n" +
                "      </ol>\n" +
                "    </div>\n" +
                "\n" +
                "    <div class=\"links\">\n" +
                "      <a class=\"link-btn primary\" href=\"https://github.com/AndrewHayesGraves/GamektPro\" target=\"_blank\">🐉 GitHub Gamekt Pro</a>\n" +
                "      <a class=\"link-btn\" href=\"https://discord.gg/2hKv4VfZfE\" target=\"_blank\">💬 Discord</a>\n" +
                "      <a class=\"link-btn\" href=\"https://meli.la/2GBs9LR\" target=\"_blank\">🛒 Loja</a>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";

            byte[] bytes = html.getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
}
