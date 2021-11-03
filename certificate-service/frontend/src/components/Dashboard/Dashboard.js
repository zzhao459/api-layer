import { InputLabel } from '@mui/material';
import { Component } from 'react';
import BasicMenu from "../Menu/BasicMenu";
import VerifyAccessPanel from "../VerifyAccessPanel/VerifyAccessPanel";

export default class Dashboard extends Component {
    render() {
        return (
            <div>
                <BasicMenu></BasicMenu>
                <VerifyAccessPanel></VerifyAccessPanel>
            </div>
        );
    }
}

