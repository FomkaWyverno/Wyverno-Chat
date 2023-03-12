const chat = document.querySelector('ul');

const socket = new WebSocket('ws://localhost:2929')

socket.addEventListener('open', e => {
    console.log('WebSocket connection established.')
});

socket.addEventListener('message', e=> {
    const data = JSON.parse(e.data);

    console.log(data)

    if (data.type === 'html') {
        chat.innerHTML = `${chat.innerHTML}\n${data.content}`;
    } else if (data.type === 'videoPlayback') {
        const videoPlayback = JSON.parse(data.content);
        
        if (videoPlayback.type === 'VIEW_COUNT') {
            console.log('Viewers: ' + videoPlayback.content);
        } else if (videoPlayback.type === 'STREAM_UP') {
            console.log('Stream start!')
        } else if (videoPlayback.type === 'STREAM_DOWN') {
            console.log('Stream offline!')
        }
    }
})