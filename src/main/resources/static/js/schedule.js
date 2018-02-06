$(function () {
    $(".update").click(function(){
        var time_con = $(this).parents().siblings(".time_express").text();
        var jobid = $(this).siblings(".job_id").val();
        var updatetype = $(this).siblings(".update_type").val();
        var jobname = $(this).siblings(".job_name").val();
        var jobgroup = $(this).siblings(".job_group").val();
        $(".time_express_val").attr("value",time_con);
        $(".jobid").attr("value",jobid);
        $(".updatetype").attr("value",updatetype);
        $(".jobname").attr("value",jobname);
        $(".jobgroup").attr("value",jobgroup);
    });
});

function forbid(obj){
    var Id = $(obj).parent().find('input[id^="jobId"]').val();
    var jobName = $(obj).parent().find('input[id^="jobName"]').val();
    var jobGroup = $(obj).parent().find('input[id^="jobGroup"]').val();
    if(confirm('确定禁用该任务吗')){
        location.href="action.htm?id="+Id+"&jobStatus=0"+"&jobName="+jobName+"&jobGroup="+jobGroup+"&del=0";
        return true;
    }else{
        return false;
    }
}

function deleterw(obj){
    var Id = $(obj).parent().find('input[id^="jobId"]').val();
    var jobName = $(obj).parent().find('input[id^="jobName"]').val();
    var jobGroup = $(obj).parent().find('input[id^="jobGroup"]').val();
    if(confirm('确定禁用并删除该任务吗')){
        location.href="action.htm?id="+Id+"&jobStatus=0"+"&jobName="+jobName+"&jobGroup="+jobGroup+"&del=1";
        return true;
    }else{
        return false;
    }
}

function enable(obj){
    var Id = $(obj).parent().find('input[id^="jobId"]').val();
    var jobName = $(obj).parent().parent().find('td[id^="jobName"]').html();
    var jobGroup = $(obj).parent().parent().find('td[id^="jobGroup"]').html();
    if(confirm('确定启用该任务吗')){
        location.href="action.htm?id="+Id+"&jobStatus=1"+"&jobName="+jobName+"&jobGroup="+jobGroup+"&del=0";
        return true;
    }else{
        return false;
    }
}

function update(){
    var jobid =$(".jobid").val();
    var updatetype=$(".updatetype").val();
    var cronExpression=$(".time_express_val").val();
    var jobname=$(".jobname").val();
    var jobgroup=$(".jobgroup").val();
    if('0'==updatetype){
        location.href="updatecronExpression.htm?id="+jobid+"&cronExpression="+cronExpression;
    }
    else{
        location.href='update.htm?id='+jobid+'&cronExpression='+cronExpression+'&jobName='+jobname+'&jobGroup='+jobgroup+'&jobStatus=1';
    }
}

function deletebyid(obj){
    var jobid = $(obj).parent().find('input[id^="jobId"]').val();
    if(confirm('确定删除该任务吗')){
        location.href="deletejob.htm?id="+jobid;
        return true;
    }else {
        return false;
    }
}

function create(obj) {

    
}