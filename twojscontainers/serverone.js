const http = require('http');

const hostname = '127.0.0.1';
const port = 443;

const server = http.createServer((req, res) => {
    
      const data = JSON.stringify({
          msg: 'This is a request from serverone',
       });
      
      const postoptions = {
        hostname: '127.0.0.1',
        port: 3000,
        path: '/',
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Content-Length': data.length,
        },
        body: data,
      };
      
      const getoptions = {
          hostname: '127.0.0.1',
          port: 3000,
          path: '/testmessage',
          method: 'GET',
        };
        
    if (req.method=='GET'){
        const req = http.request(postoptions, res => {
          //console.log(`post statusCode: ${res.statusCode}`);
          res.on('data', d => {
            process.stdout.write(d);
          });
        
          if (res.statusCode==200){
              const req = http.request(getoptions, res => {
                console.log('starting new');
                console.log(`get statusCode: ${res.statusCode}`);

              });
          }


        });
        req.on('error', error => {
            console.error(error);
          });
        req.write(data);
        req.end();
        }


    // //starting here
     //res.statusCode = 200;
     //res.setHeader('Content-Type', 'text/plain');
     res.end('This is server one');
});


server.listen(port, hostname, () => {
    console.log(`Server running at http://${hostname}:${port}/`);
  });