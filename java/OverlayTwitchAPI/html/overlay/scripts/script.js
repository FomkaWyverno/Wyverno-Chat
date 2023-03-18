const chat = document.querySelector('.messages');
const viewers = document.querySelector('.viewers')
const status_stream = document.querySelector('.status-stream')


const socket = new WebSocket('ws://localhost:2929')

socket.addEventListener('open', e => {
    console.log('WebSocket connection established.')
});

socket.addEventListener('message', e=> {
    const data = JSON.parse(e.data);

    console.log(data)

    if (data.type === 'messageHTML') {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message');
        messageElement.innerHTML = data.content;
        chat.appendChild(messageElement);
        messageElement.scrollIntoView();
    } else if (data.type === 'reward_block') {
        const reward_block = document.createElement('div');
        reward_block.classList.add('reward-block')
        reward_block.innerHTML = data.content;
        chat.appendChild(reward_block);
        reward_block.scrollIntoView();
    } else if (data.type === 'reward_message') {
        const reward_message = document.createElement('div');
        reward_message.classList.add('reward-message')
        reward_message.innerHTML = data.content;
        chat.appendChild(reward_message);
        reward_message.scrollIntoView();
    } else if (data.type === 'videoPlayback') {
        const videoPlayback = JSON.parse(data.content);
        
        if (videoPlayback.type === 'VIEW_COUNT') {
            console.log('Viewers: ' + videoPlayback.content);
            viewers.textContent = videoPlayback.content;
            status_stream.textContent = 'Online';
        } else if (videoPlayback.type === 'STREAM_UP') {
            console.log('Stream start!')
        } else if (videoPlayback.type === 'STREAM_DOWN') {
            console.log('Stream offline!')
        }
    }
})