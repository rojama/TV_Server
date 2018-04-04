//修复LigerUI Form 重置问题
function ligerFormReset(object)
{
	 
	var g=object,p=object.options,v=g.validator;
	object.form[0].reset();
	if(v!=null)
	{
		v.resetForm();
	}
	g.data={};//清空历史遗留值;
	
	  $(p.fields).each(function (fieldIndex, field)
      {
          var name = field.name, textField = field.textField, editor = g.editors[fieldIndex];
          if(editor.control.attr && editor.control.attr('type')==='hidden') {
          	editor.control.val('');
          }
          if (!editor) return;
          if(editor.control && editor.control.selectValue) {
          	editor.control.selectValue('');
          }
      });
      g.valid();
}