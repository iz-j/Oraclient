/*!
 * Text Assist
 * 
 * version: 0.1
 * Copyright (c) 2015 iz-j
 */
var TextAssist=function(e,t){"use strict";function n(){w=document.createElement("div"),w.className="textassist",k=document.createElement("ul"),k.tabIndex=-1,k.style.display="none",v(k,F),N.ulClassName?k.className=N.ulClassName:v(k,D),w.appendChild(k),L=document.createElement("div"),v(L,q),w.appendChild(L),document.body.appendChild(w)}function a(e){switch(e.keyCode){case 8:m()&&(S?l():x());break;case 9:break;case 13:E?(e.preventDefault(),h()):x();break;case 27:x();break;case 32:e.ctrlKey?(e.preventDefault(),m()?x():l()):x();break;case 38:E&&(e.preventDefault(),u());break;case 40:E&&(e.preventDefault(),d());break;case 37:case 39:m()&&l();break;case 33:case 34:case 35:case 36:x();break;default:m()&&l()}}function o(e){e.relatedTarget!==k&&x()}function i(e){for(var t=e.target;t&&t!==k;){if("A"===e.target.tagName&&t.dataset.source){H=t,h();break}t=t.parentNode}}function l(){M&&clearTimeout(M),M=setTimeout(s,N.delayMills)}function s(){for(E=!1,H=null;k.lastChild;)k.removeChild(k.lastChild);var e=document.createElement("li");e.innerHTML=N.loadingHTML,k.appendChild(e);var t=g();k.style.left=t.left+"px",k.style.top=t.top+"px",k.style.display="block",r(),N.find(S,c)}function r(){var e=T.value.slice(0,T.selectionStart),t=e.match(j);S=t?t[2]:""}function c(e){return e&&0!==e.length?(k.removeChild(k.firstChild),e.forEach(function(e){var t=document.createElement("li"),n=document.createElement("a");t.appendChild(n),N.liClassName&&(t.className=N.liClassName),N.anchorClassName?n.className=N.anchorClassName:v(n,W),n.dataset.source=e,n.innerHTML=N.item(e,S),k.appendChild(t),H||(f(n),k.scrollTop=0)}),void(E=!0)):void(k.firstChild.innerHTML=N.noneHTML)}function d(){H.parentNode.nextSibling&&f(H.parentNode.nextSibling.firstChild)}function u(){H.parentNode.previousSibling&&f(H.parentNode.previousSibling.firstChild)}function f(e){N.anchorClassName||(H&&v(H,W),v(e,A)),N.activeClassName&&(H&&H.parentNode.classList.remove(N.activeClassName),e.parentNode.classList.add(N.activeClassName)),H=e,p()}function p(){var e=k.scrollTop,t=k.offsetHeight+k.scrollTop-H.parentNode.offsetHeight;H.offsetTop<e?k.scrollTop=H.offsetTop:H.offsetTop>t&&(k.scrollTop=k.scrollTop+(H.offsetTop-t))}function h(){var e=H.dataset.source,t=N.beforeFix?N.beforeFix(e):e,n=T.value.slice(0,T.selectionStart),a=T.value.slice(T.selectionStart,T.value.length);n=n.replace(new RegExp(S+"$"),t),T.value=n+a,T.focus(),T.selectionStart=n.length,T.selectionEnd=n.length,x(),N.afterFix&&N.afterFix(t)}function v(e,t){Object.keys(t).forEach(function(n){e.style[n]=t[n]})}function m(){return"none"!==k.style.display}function g(){var e=document.defaultView.getComputedStyle(T,"");Q.forEach(function(t){L.style[t]=e[t]}),L.style.overflow="auto";var t=T.value,n=t.slice(0,T.selectionStart),a=t.slice(T.selectionStart,T.selectionEnd),o=t.slice(T.selectionEnd,t.length);a||(a="|"),L.innerHTML=y(n)+'<span class="textassist-caret">'+y(a)+"</span>"+y(o),L.scrollLeft=T.scrollLeft,L.scrollTop=T.scrollTop;var i=L.querySelector(".textassist-caret"),l=b(T),s=b(L),r=b(i);return{left:l.left+(r.left-s.left),top:l.top+(r.top-s.top+i.offsetHeight)}}function y(e){return e.replace(/[&"<>]/g,function(e){return R[e]}).replace(/\r?\n/g,"<br />")}function b(e){var t=0,n=0;do t+=e.offsetTop||0,n+=e.offsetLeft||0,e=e.offsetParent;while(e);return{top:t,left:n}}function x(){k.style.display="none",E=!1}function C(){T.removeEventListener("keydown",a),T.removeEventListener("blur",o),k.removeEventListener("click",i),T=null,N=null,w.remove(),w=null,k=null,L=null,H=null}var N={find:function(e,t){throw'"find" function is required!'},ulClassName:null,liClassName:null,anchorClassName:null,activeClassName:null,item:function(e,t){return e},loadingHTML:'<a href="javasript:void(0)">Loading...</a>',noneHTML:'<a href="javasript:void(0)">No items...</a>',delayMills:200,beforeFix:function(e){return e},afterFix:function(e){}};Object.keys(N).forEach(function(e){e in t&&(N[e]=t[e])});var T=e,w=null,k=null,L=null,E=!1,S=null,H=null,M=null,j=/(^|\s)([^\s]*)$/,F={position:"absolute",overflowX:"hidden",overflowY:"auto"},D={"list-style":"none",border:"solid 1px silver",background:"white",boxShadow:"0px 3px 6px gray",padding:"4px",margin:"2px",maxHeight:"120px"},W={whiteSpace:"nowrap",cursor:"default",display:"block",padding:"2px 20px 2px 4px",background:"white",color:"black"},A={background:"gray",color:"white"},q={visibility:"hidden",position:"absolute",left:"0px",top:"0px",whiteSpace:"nowrap"};T.wrap="off",n(),T.addEventListener("keydown",a),T.addEventListener("blur",o),k.addEventListener("click",i);var Q=["width","height","borderBottomWidth","borderLeftWidth","borderRightWidth","borderTopWidth","fontFamily","fontSize","fontStyle","fontVariant","fontWeight","letterSpacing","wordSpacing","lineHeight","textDecoration","paddingBottom","paddingLeft","paddingRight","paddingTop"],R={"&":"&amp;",'"':"&quot;","<":"&lt;",">":"&gt;"};return{hide:x,destroy:C}};window.jQuery&&!function(e){var t=function(t){return this.each(function(){var n=e(this),a=n.data("textassist");a||n.data("textassist",new TextAssist(this,t))})},n={destroy:function(){return this.each(function(){var t=e(this),n=t.data("textassist");n&&(n.destroy(),t.removeData("textassist"))})}};e.fn.textassist=function(a){return n[a]?n[a].apply(this,Array.prototype.slice.call(arguments,1)):"object"!=typeof a&&a?(e.error("Method "+a+" does not exist on jQuery.textassist"),this):t.apply(this,arguments)}}(jQuery);