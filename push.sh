#!/bin/bash
set -e

export GIT_AUTHOR_NAME="Gamekt Pro"
export GIT_AUTHOR_EMAIL="gamektpro@gmail.com"
export GIT_COMMITTER_NAME="Gamekt Pro"
export GIT_COMMITTER_EMAIL="gamektpro@gmail.com"

PAT="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"
REPO="https://${PAT}@github.com/dtenorio921-pixel/Gamekt-pro.0.9.9.10.git"

echo "==> Configurando git..."
git config user.email "gamektpro@gmail.com"
git config user.name "Gamekt Pro"

echo "==> Configurando remote..."
git remote remove gamektpro 2>/dev/null || true
git remote add gamektpro "$REPO"

echo "==> Adicionando arquivos..."
git add .

echo "==> Commitando..."
git commit -m "Gamekt Pro v0.9.9.10 - Dragao realista c/ controle, icone, ADM, Mercado Livre, TTS, Otimizacao Termica"

echo "==> Enviando para GitHub..."
git push -u gamektpro HEAD:main

echo ""
echo "PRONTO! Codigo enviado com sucesso para:"
echo "https://github.com/dtenorio921-pixel/Gamekt-pro.0.9.9.10"
