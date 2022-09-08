const http = require('http');


const hostname = '127.0.0.1';
const port = 3000;

const getoptions = {
    hostname: '127.0.0.1',
    port: 3000,
    path: '/testmessage',
    method: 'GET',
  };

const server = http.createServer(getoptions,(req, res) => {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'application/json');
  res.on('data', d => {
    process.stdout.write(d);
  });

  req.on('error', error => {
    console.error(error);
  });

  if(req.method=='POST'){
    let data = '';
    req.on('data', chunk => {
      data += chunk;
    });
    req.on('end', () => {
      console.log(JSON.parse(data).msg); 
      res.end('This is a response from servertwo');
    });
    
  }else{
    res.end('This is server two');
  }
  
});

server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});