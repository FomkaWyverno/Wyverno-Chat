const body = document.querySelector('body');

const socket = new WebSocket('ws://localhost:2929')

socket.addEventListener('open', e => {
    console.log('WebSocket connection established.')
});

socket.addEventListener('message', e=> {
    const data = JSON.parse(e.data);

    console.log(data)

    if (data.type === 'html') {
        body.innerHTML = `${body.innerHTML}\n${data.content}`;
    }
})