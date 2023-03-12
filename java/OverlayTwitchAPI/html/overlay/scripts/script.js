const chat = document.querySelector('ul');
const viewers = document.querySelector('.viewers')
const status_stream = document.querySelector('.status-stream')


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
            viewers.textContent = videoPlayback.content;
            status_stream.textContent = 'Online';
        } else if (videoPlayback.type === 'STREAM_UP') {
            console.log('Stream start!')
            status_stream.textContent = 'Online'
            viewers.textContent = '0'
        } else if (videoPlayback.type === 'STREAM_DOWN') {
            console.log('Stream offline!')
            status_stream.textContent = 'Offline'
            viewers.textContent = '-';
        }
    }
})