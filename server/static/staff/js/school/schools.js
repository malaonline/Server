function showImages(schoolId){
  var imgs = $('#hiddenImages'+schoolId).find('img');
  var cont = $('#imgContent');
  cont.empty();
  cont.append(imgs.clone());
  $('#imageModal').modal({backdrop: 'static', keyboard: false});
}
