export interface Comment {
  //id: string;
  user:{
    id: string,
    username: string
  };
  textBody:string;
  timestamp:string;//iso date string
}
