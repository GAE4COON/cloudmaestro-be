cd ~/deploy-fe
rm -rf node_modules
rm package-lock.json
npm install
nohup npm start &
