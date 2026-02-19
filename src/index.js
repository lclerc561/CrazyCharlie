const express = require('express');
const app = express();
const port = 3000;

app.get('/', (req, res) => {
  res.send('<h1>Ça marche !</h1><p>Le DACS a bien fait son boulot, le site est en ligne via Docker.</p>');
});

app.listen(port, () => {
  console.log(`Serveur prêt sur http://localhost:${port}`);
});
