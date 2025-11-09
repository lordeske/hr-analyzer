import React from "react";


export default function Forbidden() {




  return (
    <div style={{
      minHeight:"60vh", display:"grid", placeItems:"center",
      color:"#fff", background:"#0b0b12", fontFamily:"Poppins, system-ui"
    }}>
      <div style={{textAlign:"center"}}>
        <h1 style={{margin:0, fontSize:32, fontWeight:800}}>403 – No access</h1>
        <p style={{opacity:.8}}>You don’t have permission to view this page.</p>
      </div>
    </div>
  );
}
