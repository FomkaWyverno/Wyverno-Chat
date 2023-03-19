const { ipcRenderer } = require('electron');

const collapse_button = document.querySelector('.list-control-panel__collapse')
const maximize_minimize_button = document.querySelector('.list-control-panel__maximize-minimize')
const close_button = document.querySelector('.list-control-panel__close')

collapse_button.addEventListener('click', () => { // Сворачиваем окно
    console.log('click collapse')
    ipcRenderer.send('window.overlay.collapse')
});
maximize_minimize_button.addEventListener('click', () => { // Маштабируем или минимизируем
    console.log('click maximize_minimize');
    ipcRenderer.send('window.overlay.maximize-minimize');
});
close_button.addEventListener('click', () => { // Закрываем окно
    console.log('click close');
    ipcRenderer.send('window.overlay.close')
});

ipcRenderer.on('window.overlay.maximize',() => 
{
    console.log('Maximize')
    maximize_minimize_button.firstElementChild.classList.add('minimize')
});

ipcRenderer.on('window.overlay.minimize',() =>
{
    console.log('Minimize')
    maximize_minimize_button.firstElementChild.classList.remove('minimize')
});