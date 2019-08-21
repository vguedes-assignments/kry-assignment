const listContainer = document.querySelector('#service-list');
let servicesRequest = new Request('/service');

const refreshServicesList = () => {
  console.log("refreshing services")
  fetch(new Request('/service'))
    .then(function (response) { return response.json(); })
    .then(function (serviceList) {
      serviceList.forEach(service => {
        var li = document.getElementById(service.url);
        var label = service.name ? `${service.name} (${service.url})` : service.url;
        li && li.replaceChild(document.createTextNode(label + ' - added : ' + service.addedAt + ' - status : ' + service.status), li.childNodes[0]);
      });
    });
}

fetch(servicesRequest)
  .then(function (response) { return response.json(); })
  .then(function (serviceList) {
    serviceList.forEach(service => {
      var li = document.createElement("li");
      li.id = service.url;
      var label = service.name ? `${service.name} (${service.url})` : service.url;
      li.appendChild(document.createTextNode(label + ' - added : ' + service.addedAt + ' - status : ' + service.status));
      var deleteButton = document.createElement("button");
      deleteButton.style.marginLeft = "50px"
      deleteButton.innerHTML = "Delete";
      deleteButton.onclick = deleteService(service.url)
      li.appendChild(deleteButton);
      listContainer.appendChild(li);
    });
  });

setInterval(refreshServicesList, 30 * 1000);

const  deleteService = (url) => {
  return () => {
    fetch('/service', {
      method: 'delete',
      headers: {
        'Accept': 'application/json, text/plain, */*',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ url })
    }).then(res => location.reload());
  }
}


const saveButton = document.querySelector('#post-service');
saveButton.onclick = evt => {
  let url = document.querySelector('#url').value;
  let name = document.querySelector('#name').value;
  if(!isValidUrl(url)){
    var errorMessage = document.getElementById('error-message-invalid-url');
    errorMessage.style.display = "inline-block";
    return;
  }
  fetch('/service', {
    method: 'post',
    headers: {
      'Accept': 'application/json, text/plain, */*',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ url: url , name: name })
  }).then(res => location.reload());
}

const dismissError = document.querySelector('#dismiss-error');

dismissError.onclick = evt => {
  document.getElementById('error-message-invalid-url').style.display = "none";
}

const isValidUrl = (string) => {
  try {
    new URL(string);
    return true;
  } catch (_) {
    return false;  
  }
}